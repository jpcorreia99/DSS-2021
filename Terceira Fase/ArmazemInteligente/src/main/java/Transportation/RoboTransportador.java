package Transportation;

import Business.Armazem.Robo.EstadoRobo;
import Database.NotificacaoDAO;
import Database.PaleteDAO;
import Database.RoboDAO;
import Database.RotaDAO;
import Util.Coordenadas;

import java.util.Scanner;

public class RoboTransportador {
    private  int id;
    //coordenadas atiais do robo;
    private  Coordenadas coordenadas;
    //id da zona onde o robo estaciona
    private  int zonaEstacionamento;
    // prateleira a que se encontra destinado
    private  int idPrateleira;
    // palete que carrega
    private  int idPalete;
    // estado do robo
    private  EstadoRobo estado;

    // DAOs necessários para comunicar com a BD
    private RoboDAO  roboDAO;
    private RotaDAO  rotaDAO;
    private NotificacaoDAO notificacaoDAO;

    public RoboTransportador(int id, int x, int y, int zonaEstacionamento ,int idPrateleira,int idPalete, EstadoRobo estado) {
        this.id = id;
        this.coordenadas = new Coordenadas(x,y);
        this.zonaEstacionamento = zonaEstacionamento;
        this.idPrateleira = idPrateleira;
        this.idPalete = idPalete;
        this.estado = estado;
        this.roboDAO = RoboDAO.getInstance();
        this.rotaDAO = RotaDAO.getInstance();
        this.notificacaoDAO = NotificacaoDAO.getInstance();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Indique o id do robo: ");
        int idRobo = sc.nextInt();

        RoboDAO roboDAO = new RoboDAO();
        if(roboDAO.existeRobo(idRobo)){
            RoboTransportador roboTransportador = roboDAO.getRoboTransportador(idRobo);
            roboTransportador.run();
        }else{
            System.out.println("Robo não existe!");
        }
    }

    public void run(){
//        while (true){
//            if(rotaDAO)
//        }
    }

    @Override
    public String toString() {
        return "Robo{" +
                "id=" + id +
                ", coordenadas=" + coordenadas +
                ", zonaEstacionamento=" + zonaEstacionamento +
                ", idPrateleira=" + idPrateleira +
                ", idPalete=" + idPalete +
                ", estado=" + estado +
                '}';
    }
}
