package Requests;

import java.io.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


import javax.imageio.ImageIO;

import Business.Armazem.Stock.Palete;
import Database.PaleteDAO;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;


public class LeitorCodigosQR implements Runnable {

//    Here are the basic steps required to implement a watch service:
//
//    - Create a WatchService "watcher" for the file system.
//    - For each directory that you want monitored, register it with the watcher. When registering a directory, you specify the type of events for which you want notification. You receive a WatchKey instance for each directory that you register.
//    - Implement an infinite loop to wait for incoming events. When an event occurs, the key is signaled and placed into the watcher's queue. Retrieve the key from the watcher's queue. You can obtain the file name from the key.
//    - Retrieve each pending event for the key (there might be multiple events) and process as needed.
//    - Reset the key, and resume waiting for events.
//    - Close the service: The watch service exits when either the thread exits or when it is closed (by invoking its closed method).

    private final PaleteDAO paletesAGuardar;
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private Boolean working; // DEVERÁ SER TORNADO FALSO PELA THREAD PRINCIPAL PARA ACABAR COM O TRABALHO DESTA THREAD

    public LeitorCodigosQR( Lock lockPaletes, Condition conditionNovaPalete) throws IOException {
        this.paletesAGuardar = PaleteDAO.getInstance();
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        File sourceDir = new File("src/main/resources");
        Path sourceDirPath = Paths.get(sourceDir.toURI());
        this.working=true;

        WatchKey key = sourceDirPath.register(watcher, ENTRY_CREATE); // verifica a criação de novos ficheiros na diretoria
        keys.put(key, sourceDirPath);
    }

    /**
     * Processa o evento assinalado na key, ou seja, se algum códigoQR foi criado na diretoria assinalada
     *
     */
    public void run() {
        System.out.println("QRCode Reader funcional!");
        // loop infinito - está sempre a verificar se apareceu algum ficheiro novo
        while(working) {
            // espera que a chave do watcher da diretoria seja assinalada
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            // verifica qual é a diretoria associada à chave
            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            // por cada evento de criação irá tentar ler e adicionar uma palete
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path name = ev.context();
                String childPath = dir.resolve(name).toString();

                // print out event
//                System.out.format("%s: %s\n", event.kind().name(), childPath);

                // tendo o path do novo ficheiro, tentar criar a palete
                try {
                    String material = readQR(childPath);
                    paletesAGuardar.addNovaPalete(material);
//                    System.out.println("Palete registada: "+material);
                } catch (NotFoundException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // se a diretoria desaparecer, remove a chave
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // se todas as diretorias desaparecerem, termina-se a execução
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
        try {
            watcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
      * @param path path para um código QR
     * @return material representado no QRCode
     * @throws IOException
     * @throws NotFoundException
     */
    public static String readQR(String path)
            throws IOException,
            NotFoundException, InterruptedException {

        Thread.sleep(250); // necessário um pequeno sleep por questões de sincronização causadas pelo watcher no repositório

        FileInputStream fis = new FileInputStream(path);

        BinaryBitmap binaryBitmap
                = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(new BufferedInputStream(fis)))));

        Result result
                = new MultiFormatReader().decode(binaryBitmap);

        fis.close();

        return result.getText();
    }

    public void desliga(){
        this.working=false;
    }
}
