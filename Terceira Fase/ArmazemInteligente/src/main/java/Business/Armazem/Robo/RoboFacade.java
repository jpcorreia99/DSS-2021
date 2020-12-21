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
        System.out.println("Rota enviada ao robo "+idRobo+": "+sb.toString());
    }

    public ResultadosMovimentoRobos moveRobos(){
        ResultadosMovimentoRobos resultadosMovimentoRobos = new ResultadosMovimentoRobos();
        List<Integer> idsRobosEmTransito = rotaDAO.getIdsRobosEmTransito();
        Map<Integer,Robo> robosEmTransito = roboDAO.getRobos(idsRobosEmTransito);

//        System.out.print("Robos em movimento: ");
//        for(int id : robosEmTransito.keySet()){
//            System.out.print(id+" ");
//        }
//        System.out.println("");


        for(Map.Entry<Integer,Robo> entradaIdRobo : robosEmTransito.entrySet()){
            int idRobo = entradaIdRobo.getKey();
            Robo robo = entradaIdRobo.getValue();

            Coordenadas proximoPasso = rotaDAO.getProximoPasso(idRobo);
            robo.setCoordenadas(proximoPasso);

            if(rotaDAO.rotaTerminou(idRobo)) { // terminou uma parte do transporte
                EstadoRobo estadoRobo = robo.getEstado();

                int idPrateleira = robo.getIdPrateleira();
                int idPalete = robo.getIdPalete();

                if (estadoRobo == EstadoRobo.RECOLHA) { // está a reoclher a palete
                    Notificacao notificacao = new Notificacao(robo.getId(), TipoNotificacao.RECOLHA);
                    notificacaoDAO.enviarNotificacao(notificacao, DirecionalidadeNotificacao.PARA_SERVIDOR);
                    resultadosMovimentoRobos.addPaleteRecolhida(idPalete, idPrateleira, robo.getId(), robo.getCoordenadas());
                    System.out.println("Robo "+idRobo+" está a transportar");
                } else if (estadoRobo == EstadoRobo.TRANSPORTE) { // indica que se está a deslocar para a ir entregar a palete
                    resultadosMovimentoRobos.addTuploPaleteArmazenadaPrateleira(idPalete, idPrateleira);

                    Notificacao notificacao = new Notificacao(robo.getId(), TipoNotificacao.ENTREGA);
                    notificacaoDAO.enviarNotificacao(notificacao, DirecionalidadeNotificacao.PARA_SERVIDOR);
                    resultadosMovimentoRobos.addRoboQueArmazenou(robo.getId(),
                            robo.getZonaEstacionamento(), robo.getCoordenadas());

                    robo.setIdPalete(0);
                    robo.setIdPrateleira(0);
                    robo.setEstado(EstadoRobo.RETORNO);
                    System.out.println("Robo "+idRobo+" está a retornar");
                } else { // só sobra estar a retornar, logo deve ficar livre
                    System.out.println("Robo "+idRobo+" ficou livre");
                    robo.setEstado(EstadoRobo.LIVRE);
                }
            }
        }
        roboDAO.atualizaRobos(robosEmTransito);
        return resultadosMovimentoRobos;
    }

    public ResultadosMovimentoRobos processaNotificacoes() {
        ResultadosMovimentoRobos resultadosMovimentoRobos = new ResultadosMovimentoRobos();
        List<Notificacao> notificacoes = notificacaoDAO.lerNotificacoesServidor();

        for (Notificacao notificacao : notificacoes) {
            Robo robo = roboDAO.get(notificacao.getIdRobo());

            int idPrateleira = robo.getIdPrateleira();
            int idPalete = robo.getIdPalete();
            TipoNotificacao tipoNotificacao = notificacao.getTipo();
            if (tipoNotificacao == TipoNotificacao.RECOLHA) {
                System.out.println("Notificação de recolha do robo "+robo.getId());
                resultadosMovimentoRobos.addPaleteRecolhida(idPalete, idPrateleira, robo.getId(), robo.getCoordenadas());
            } else { // TipoNotificacao.ENTREGA
                System.out.println("Notificação de entrega do robo "+robo.getId());
                resultadosMovimentoRobos.addTuploPaleteArmazenadaPrateleira(idPalete, idPrateleira);
                resultadosMovimentoRobos.addRoboQueArmazenou(robo.getId(),
                        robo.getZonaEstacionamento(), robo.getCoordenadas());

                robo.setIdPalete(0);
                robo.setIdPrateleira(0);

                roboDAO.atualiza(robo);
            }
        }

        return resultadosMovimentoRobos;


//        ResultadosMovimentoRobos resultadosMovimentoRobos = new ResultadosMovimentoRobos();
//        List<Integer> idsRobosEmTransito = rotaDAO.getIdsRobosEmTransito();
//        Map<Integer,Robo> robosEmTransito = roboDAO.getRobos(idsRobosEmTransito);
//
//        for(Map.Entry<Integer,Robo> entradaIdRobo : robosEmTransito.entrySet()){
//            int idRobo = entradaIdRobo.getKey();
//            Robo robo = entradaIdRobo.getValue();
//
//            Coordenadas proximoPasso = rotaDAO.getProximoPasso(idRobo);
//            robo.setCoordenadas(proximoPasso);
//
//            if(rotaDAO.rotaTerminou(idRobo)) { // terminou uma parte do transporte
//                EstadoRobo estadoRobo = robo.getEstado();
//
//                int idPrateleira = robo.getIdPrateleira();
//                int idPalete = robo.getIdPalete();
//
//                if (estadoRobo == EstadoRobo.RECOLHA) { // está a reoclher a palete
//                    Notificacao notificacao = new Notificacao(robo.getId(), TipoNotificacao.RECOLHA);
//                    notificacaoDAO.enviarNotificacao(notificacao,DirecionalidadeNotificacao.PARA_ROBO);
//                    resultadosMovimentoRobos.addPaleteRecolhida(idPalete, idPrateleira, robo.getId(), robo.getCoordenadas());
//                } else if (estadoRobo == EstadoRobo.TRANSPORTE) { // indica que se está a deslocar para a ir entregar a palete
//                    resultadosMovimentoRobos.addTuploPaleteArmazenadaPrateleira(idPalete, idPrateleira);
//
//                    Notificacao notificacao = new Notificacao(robo.getId(), TipoNotificacao.ENTREGA);
//                    notificacaoDAO.enviarNotificacao(notificacao, DirecionalidadeNotificacao.PARA_SERVIDOR);
//                    resultadosMovimentoRobos.addRoboQueArmazenou(robo.getId(),
//                            robo.getZonaEstacionamento(), robo.getCoordenadas());
//
//                    robo.setIdPalete(0);
//                    robo.setIdPrateleira(0);
//                    robo.setEstado(EstadoRobo.RETORNO);
//                } else { // só sobra estar a retornar, logo deve ficar livre
//                    robo.setEstado(EstadoRobo.LIVRE);
//                }
//            }
//        }
//        roboDAO.atualizaRobos(robosEmTransito);
//        return resultadosMovimentoRobos;
    }
}
