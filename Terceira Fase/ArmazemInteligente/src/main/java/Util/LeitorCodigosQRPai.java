package Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LeitorCodigosQRPai {
    static List<String> lido = new ArrayList<>();
    static LeitorCodigosQR leitorCodigosQR = new LeitorCodigosQR();

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            try {
                while (true) {
                    Socket socket = ss.accept(); // bloqueia à espera de alguma conexão
                    lido.add(leitorCodigosQR.leCodigoQR(socket));
                }
            } finally {
                ss.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
