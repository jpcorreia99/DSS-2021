package Transportation;

import Util.EstadoRobo;
import Database.*;
import Util.Coordenadas;
import Util.DirecionalidadeNotificacao;
import Util.Notificacao;
import Util.TipoNotificacao;

public class RoboTransportador implements Runnable {
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

    private boolean funcional;

    // DAOs necessários para comunicar com a BD
    private RoboTransportadorDAO roboTransportadorDAO;
    private RotaDAO  rotaDAO;
    private NotificacaoDAO notificacaoDAO;

    public RoboTransportador(int id, int x, int y, int zonaEstacionamento, int idPrateleira, int idPalete, EstadoRobo estadoRobo) {
        this.id = id;
        this.coordenadas = new Coordenadas(x,y);
        this.zonaEstacionamento = zonaEstacionamento;
        this.idPrateleira = idPrateleira;
        this.idPalete = idPalete;
        this.estado = estadoRobo;
        this.funcional = true;

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

    public void setIdPrateleira(int idPrateleira) {
        this.idPrateleira = idPrateleira;
    }

    public void setIdPalete(int idPalete) {
        this.idPalete = idPalete;
    }

    public void setEstado(EstadoRobo estado) {
        this.estado = estado;
    }


    public void run() {
        while(funcional) {
            if (notificacaoDAO.recebeuNovaRota(this.id)) {
                // robô vai procurar nova informação relativa a si na tabela robos
                roboTransportadorDAO.leDadosRoboTransportador(this);
            }

            Coordenadas proximoPasso;
            // para evitar situações imprevistas, é devolvida a coordenada (0,0) em situações em que se falha na leitura da rota
            if ((proximoPasso = rotaDAO.getProximoPasso(this.id)) != null && !(proximoPasso.getX()==0 && proximoPasso.getY()==0)) {
                this.coordenadas = proximoPasso;
                if (rotaDAO.rotaTerminou(this.id)) {
                    if (this.estado == EstadoRobo.RECOLHA) {
                        notificaRecolhaPalete(); // use case :)
                    } else if (this.estado == EstadoRobo.TRANSPORTE) {
                        notificaEntregaPalete(); // use case :)
                    } else {
                        this.estado = EstadoRobo.LIVRE;
                    }
                }
                // inserir dados atualizados na BD
                this.roboTransportadorDAO.atualizaDadosRoboTransportador(this);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void desliga(){
        this.funcional=false;
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
