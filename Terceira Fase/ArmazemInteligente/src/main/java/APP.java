import Database.DBConnect;
import Database.ConnectionPool;
import Database.RoboTransportadorDAO;
import Requests.LeitorCodigosQR;
import Transportation.RoboTransportador;

import View.UI;

import java.io.IOException;
import java.sql.SQLException;



public class APP {
    public static void main(String[] args) {
        try {
            // setup da base de dados e da view para se poderem enviar mensagems acerca do começo do programa
            DBConnect.setupBD();
            ConnectionPool.initialize();
            UI ui = new UI();

            // iniciar o funcionamento do leitor de códigos QR externo
            LeitorCodigosQR leitorCodigosQR = new LeitorCodigosQR();
            Thread threadLeitorCodigosQR = new Thread(leitorCodigosQR);
            threadLeitorCodigosQR.start();
            ui.show("Leitor de códigos QR funcional!");

            // iniciar o funcionamento dos robos externos
            Thread[] threadsRobosExternos = new Thread[4];
            RoboTransportador[] robosExternos = new RoboTransportador[4];
            RoboTransportadorDAO roboTransportadorDAO = RoboTransportadorDAO.getInstance();
            for (int i = 0; i <= 3; i++) {
                RoboTransportador robo = roboTransportadorDAO.getRoboTransportador(i+1);
                robosExternos[i] = robo;
            }

            for(int i = 0 ; i< robosExternos.length;i++){
                Thread threadRobo = new Thread(robosExternos[i]);
                threadsRobosExternos[i] = threadRobo;
                threadRobo.start();
                ui.show("Robo "+(i+1)+" funcional!");
            }

            // inicia a visualização do programa e interação com o user
            ui.inicia();


            // fim do programa
            threadLeitorCodigosQR.interrupt(); // não é possível criar uma flag de interrupção pois o watch service bloqueia à espera
            ui.show("Leitor de códigos códigos QR desligado!");


            for (int i = 0; i<=3; i++){
                robosExternos[i].desliga();
                try {
                    threadsRobosExternos[i].join();
                    ui.show("Robo "+(i+1)+" desligado!");
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Problema com a ligação à base de dados");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
