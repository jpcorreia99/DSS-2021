package Business.Armazem.Robo;

import Database.RoboDAO;
import Business.Armazem.IRobo;
import Util.Coordenadas;
import Util.EstadoRobo;
import Util.ResultadosMovimentoRobos;
import Util.Tuple;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class RoboFacade implements IRobo{
    Map<Integer, List<Coordenadas>> rotas;
    RoboDAO roboDAO;

    public RoboFacade(){
        this.roboDAO = RoboDAO.getInstance();
        this.rotas = new HashMap<>();
    }

    public boolean existeRoboDisponivel(){
        return this.roboDAO.existeRoboDisponivel();
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
        this.rotas.put(idRobo,percurso);
    }

    public ResultadosMovimentoRobos moveRobos(){
        ResultadosMovimentoRobos resultadosMovimentoRobos = new ResultadosMovimentoRobos();
        Set<Integer> idsRobosEmTransito = rotas.keySet();
        System.out.println("Robos em movimento:");
        for(int id : idsRobosEmTransito){
            System.out.print(id+", ");
        }
        System.out.println("\n");
        Map<Integer,Robo> robosEmTransito = roboDAO.getRobos(idsRobosEmTransito);
        System.out.println("Robos em movimento(confirmação): ");
        for(int id : robosEmTransito.keySet()){
            Robo r = roboDAO.get(id);
            System.out.print(id+", estado: "+r.getEstado()+", palete: "+ r.getIdPalete());
        }
        System.out.println("\n");

        for(Map.Entry<Integer,Robo> r : robosEmTransito.entrySet()){
            Robo robo = r.getValue();
            Coordenadas proximoPasso = rotas.get(robo.getId()).remove(0);
            robo.setCoordenadas(proximoPasso);

            if(rotas.get(robo.getId()).isEmpty()) { // terminou uma parte do transporte
                EstadoRobo estadoRobo = robo.getEstado();

                int idDestino = robo.getIdPrateleira();
                int idPalete = robo.getIdPalete();

                if (estadoRobo == EstadoRobo.RECOLHA) { // está a reoclher a palete
                    resultadosMovimentoRobos.addPaleteRecolhida(idPalete, idDestino, robo.getId(), robo.getCoordenadas());
                } else if (estadoRobo == EstadoRobo.TRANSPORTE) { // indica que se está a deslocar para a ir entregar a palete
                    resultadosMovimentoRobos.addTuploPaleteArmazenadaPrateleira(idPalete, idDestino);
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
                rotas.remove(robo.getId()); // remove a entrada o robo nas rotas
            }
        }
        roboDAO.atualizaRobos(robosEmTransito);
        return resultadosMovimentoRobos;
    }
}
