package Util;

import com.google.zxing.WriterException;

import java.io.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;


public class RequestSender {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Material a codificar em código QR (escreva sair para terminar o leitor): ");
        // Material dado como input
        String material = scanner.nextLine();
        while (!material.equals("sair")){
            // Path onde o código QR será guardado, terá uma timestamp
            ZonedDateTime date = ZonedDateTime.now();
            String timestamp = DateTimeFormatter.ofPattern("dd_MM_yyyy__hh_mm_ss").format(date);
            String path = "src/main/resources/" + timestamp.toString() + ".png";

            // Encoding charset
            String charset = "UTF-8";

            // Cria o código QR
            // guarda-lo no path indicado em cima
            try {
                criarQR(material, path, charset, 50, 50);
                System.out.println("Código QR gerado com sucesso: "+material);
            } catch (IOException | WriterException e) {
                e.printStackTrace();
            }

            System.out.print("Material a codificar em código QR (escreva sair para terminar o leitor): ");
            material = scanner.nextLine();
        }
    }

    public static void criarQR(String data, String path,
                               String charset,
                               int height, int width)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(data.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, width, height);

        MatrixToImageWriter.writeToFile(
                matrix,
                path.substring(path.lastIndexOf('.') + 1), // indica o formato (.png)
                new File(path));
    }
}
