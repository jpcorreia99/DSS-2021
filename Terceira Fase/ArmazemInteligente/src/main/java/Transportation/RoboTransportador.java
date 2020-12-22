package Transportation;

import Business.Armazem.Robo.EstadoRobo;
import Database.*;
import Util.Coordenadas;
import Util.DirecionalidadeNotificacao;
import Util.Notificacao;
import Util.TipoNotificacao;

import java.sql.SQLException;
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
    private RoboTransportadorDAO roboTransportadorDAO;
    private RotaDAO  rotaDAO;
    private NotificacaoDAO notificacaoDAO;

    public static void main(String[] args) {
        try {
            DBConnect.setupBD();
            ConnectionPool.initialize();
            Scanner sc = new Scanner(System.in);
            System.out.print("Indique o id do robo: ");
            int idRobo = sc.nextInt();

            RoboTransportadorDAO roboTransportadorDAO = RoboTransportadorDAO.getInstance();
            if(roboTransportadorDAO.existeRobo(idRobo)){
                RoboTransportador roboTransportador = roboTransportadorDAO.getRoboTransportador(idRobo);
                try {
                    roboTransportador.run();
                }  catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("Robo não existe!");
            }
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public RoboTransportador(int id, int x, int y, int zonaEstacionamento ,int idPrateleira,int idPalete, EstadoRobo estado) {
        this.id = id;
        this.coordenadas = new Coordenadas(x,y);
        this.zonaEstacionamento = zonaEstacionamento;
        this.idPrateleira = idPrateleira;
        this.idPalete = idPalete;
        this.estado = estado;
        this.roboTransportadorDAO = RoboTransportadorDAO.getInstance();
        this.rotaDAO = RotaDAO.getInstance();
        this.notificacaoDAO = NotificacaoDAO.getInstance();
    }

    public int getId() {
        return id;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public int getZonaEstacionamento() {
        return zonaEstacionamento;
    }

    public int getIdPrateleira() {
        return idPrateleira;
    }

    public int getIdPalete() {
        return idPalete;
    }

    public EstadoRobo getEstado() {
        return estado;
    }

    public void setCoordenadas(Coordenadas coordenadas) {
        this.coordenadas = coordenadas;
    }


    public void setIdPrateleira(int idPrateleira) {
        this.idPrateleira = idPrateleira;
    }

    public void setIdPalete(int idPalete) {
        this.idPalete = idPalete;
    }

    public void setEstado(EstadoRobo estado) {
        this.estado = estado;
    }


    public void run() throws InterruptedException {
        while(true) {
            System.out.println(this.coordenadas.toString());
            long start = System.currentTimeMillis();
            if (notificacaoDAO.recebeuNovaRota(this.id)) {
                System.out.println("Notif recebida");
                // robô vai procurar nova informação relativa a si na tabela robos
                roboTransportadorDAO.leDadosRoboTransportador(this);
            }

            Coordenadas proximoPasso;
            if ((proximoPasso = rotaDAO.getProximoPasso(this.id)) != null) {
                this.coordenadas = proximoPasso;
                if (rotaDAO.rotaTerminou(this.id)) {
                    if (this.estado == EstadoRobo.RECOLHA) {
                        System.out.println("Robo envia notificação de recolha");
                        notificaRecolhaPalete(); // use case :)
                        // falta atualizar informaçao
                    } else if (this.estado == EstadoRobo.TRANSPORTE) {
                        System.out.println("Robo envia notificação de entrega");
                        notificaEntregaPalete(); // use case :)
                        // falta atualizar informaçao
                    } else {
                        System.out.println("Robo Livre");
                        this.estado = EstadoRobo.LIVRE;
                    }
                }
                // inserir dados atualizados na BD
                this.roboTransportadorDAO.atualizaDadosRoboTransportador(this);
            }
            Thread.sleep(1000);
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            System.out.println("Tempo gasto: "+timeElapsed);
        }
    }

    // cria notificação para o servidor indicando que a palete foi recolhida
    private void notificaRecolhaPalete(){
        Notificacao notificacao = new Notificacao(this.id, TipoNotificacao.RECOLHA);
        notificacaoDAO.enviarNotificacao(notificacao, DirecionalidadeNotificacao.PARA_SERVIDOR);
    }

    // cria notificação para o servidor indicando que a palete foi guardada na respetiva prateleira
    private void notificaEntregaPalete(){
        Notificacao notificacao = new Notificacao(this.id, TipoNotificacao.ENTREGA);
        notificacaoDAO.enviarNotificacao(notificacao, DirecionalidadeNotificacao.PARA_SERVIDOR);
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
