package Business.Armazem.Robo;

import Database.NotificacaoDAO;
import Database.RoboDAO;
import Business.Armazem.IRobo;
import Database.RotaDAO;
import Util.*;

import java.util.*;

public class RoboFacade implements IRobo{
    RotaDAO rotaDAO;
    RoboDAO roboDAO;
    NotificacaoDAO notificacaoDAO;

    public RoboFacade(){
        this.roboDAO = RoboDAO.getInstance();
        this.rotaDAO = RotaDAO.getInstance();
        this.notificacaoDAO = NotificacaoDAO.getInstance();
    }

    public boolean existemRobosDisponiveis(){
        return this.roboDAO.existemRobosDisponiveis();
    }

    public Tuple<Integer,Coordenadas> encontraRoboLivre(int idPalete){
        return this.roboDAO.encontraRoboLivre(idPalete);
    }

    public void transmiteInfoRota(int idPalete, int idPrateleira , int idRobo, List<Coordenadas> percurso, EstadoRobo estadoATer){
        Robo robo = roboDAO.get(idRobo);
        robo.setIdPalete(idPalete);
        robo.setIdPrateleira(idPrateleira);
        robo.setEstado(estadoATer);
        roboDAO.atualiza(robo);
        rotaDAO.adicionaRota(idRobo,percurso);
        StringBuilder sb = new StringBuilder();
        for (Coordenadas coordenadas : percurso){
            sb.append(coordenadas.getX()).append(",").append(coordenadas.getY()).append("/");
        }

        Notificacao notificacao = new Notificacao(idRobo,TipoNotificacao.NOVA_ROTA);
        notificacaoDAO.enviarNotificacao(notificacao,DirecionalidadeNotificacao.PARA_ROBO);
    }

    public int getIdZonaEstacionamento(int idRobo){
        return  this.roboDAO.get(idRobo).getZonaEstacionamento();
    }
    /**
     * Função que irá ler todas as notificações enviadas ao sistema pelos robos e organizará a informação retida nelas num objeto
     * que será passado ao sistema de movo a que ele possa implementar as mudanças necessárias
     * @return
     */
    public ResultadosMovimentoRobos recolheNotificacoes() {
        ResultadosMovimentoRobos resultadosMovimentoRobos = new ResultadosMovimentoRobos();
        List<Notificacao> notificacoes = notificacaoDAO.lerNotificacoesServidor();

        for (Notificacao notificacao : notificacoes) {
            Robo robo = roboDAO.get(notificacao.getIdRobo());

            int idPrateleira = robo.getIdPrateleira();
            int idPalete = robo.getIdPalete();
            TipoNotificacao tipoNotificacao = notificacao.getTipo();
            if (tipoNotificacao == TipoNotificacao.RECOLHA) {
                resultadosMovimentoRobos.addPaleteRecolhida(idPalete, idPrateleira, robo.getId(), robo.getCoordenadas());
            } else { // TipoNotificacao.ENTREGA
                resultadosMovimentoRobos.addTuploPaleteArmazenadaPrateleira(idPalete, idPrateleira);
                resultadosMovimentoRobos.addRoboQueArmazenou(robo.getId(),
                        robo.getZonaEstacionamento(), robo.getCoordenadas());

                robo.setIdPalete(0);
                robo.setIdPrateleira(0);

                roboDAO.atualiza(robo);
            }
        }

        return resultadosMovimentoRobos;
    }

    public Coordenadas getCoordPalete(int idPalete){
        Robo roboQueTransportaPalete = this.roboDAO.getRoboQueTransportaPalete(idPalete);

        if(roboQueTransportaPalete!=null)
            return roboQueTransportaPalete.getCoordenadas();
        else
            return  new Coordenadas(0,0);
    }

    /**
     * Devolve uma lista com um tuplo contendo as coordenadas do robo e um booleano
     * que indica se ele está a ir entregar a palete ou não
     * @return
     */
    public  List<Tuple<Coordenadas, Boolean>> getInfoRobos(){
        List<Tuple<Coordenadas,Boolean>> res = new ArrayList<>();
        List<Robo> lista = roboDAO.getRobos();

        for(Robo r: lista){
            Boolean emTransporte = r.getEstado()==EstadoRobo.TRANSPORTE;
            res.add(new Tuple<>(r.getCoordenadas(), emTransporte));
        }

        return res;
    }
}
