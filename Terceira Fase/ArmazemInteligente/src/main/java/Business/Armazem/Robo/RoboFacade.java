package Business.Armazem.Robo;

import Database.RoboDAO;
import Business.Armazem.IRobo;
import Database.RotaDAO;
import Util.Coordenadas;
import Util.ResultadosMovimentoRobos;
import Util.Tuple;

import java.util.*;

public class RoboFacade implements IRobo{
    RotaDAO rotaDAO;
    RoboDAO roboDAO;

    public RoboFacade(){
        this.roboDAO = RoboDAO.getInstance();
        this.rotaDAO = RotaDAO.getInstance();
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
        roboDAO.put(idRobo,robo);
        rotaDAO.adicionaRota(idRobo,percurso);
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
                    resultadosMovimentoRobos.addPaleteRecolhida(idPalete, idPrateleira, robo.getId(), robo.getCoordenadas());
                } else if (estadoRobo == EstadoRobo.TRANSPORTE) { // indica que se está a deslocar para a ir entregar a palete
                    resultadosMovimentoRobos.addTuploPaleteArmazenadaPrateleira(idPalete, idPrateleira);
                    resultadosMovimentoRobos.addRoboQueArmazenou(robo.getId(),
                            robo.getZonaEstacionamento(), robo.getCoordenadas());

                    robo.setIdPalete(0);
                    robo.setIdPrateleira(0);
                    robo.setEstado(EstadoRobo.RETORNO);
                } else { // só sobra estar a retornar, logo deve ficar livre
                    robo.setEstado(EstadoRobo.LIVRE);
                }
            }
        }
        roboDAO.atualizaRobos(robosEmTransito);
        return resultadosMovimentoRobos;
    }
}
