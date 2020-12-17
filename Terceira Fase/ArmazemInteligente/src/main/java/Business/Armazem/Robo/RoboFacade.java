package Business.Armazem.Robo;

import Database.RoboDAO;
import Business.Armazem.IRobo;
import Database.RotaDAO;
import Util.Coordenadas;
import Util.ResultadosMovimentoRobos;
import Util.Tuple;

import java.util.*;

public class RoboFacade implements IRobo{
//    Map<Integer, List<Coordenadas>> rotas;
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

        System.out.println("Robos em movimento(confirmação): ");
        for(int id : robosEmTransito.keySet()){
            Robo r = roboDAO.get(id);
            System.out.print(id+", estado: "+r.getEstado()+", palete: "+ r.getIdPalete());
        }
        System.out.println("\n");


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
                    System.out.println("Robo " + robo.getId() + " entregou a palete");
                } else { // só sobra estar a retornar, logo deve ficar livre
                    robo.setEstado(EstadoRobo.LIVRE);
                    System.out.println("Robo " + robo.getId() + " chegou ao estacionamento");
                }
            }
        }
        roboDAO.atualizaRobos(robosEmTransito);
        return resultadosMovimentoRobos;
    }
}
