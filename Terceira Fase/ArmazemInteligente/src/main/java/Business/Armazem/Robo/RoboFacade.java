package Business.Armazem.Robo;

import Database.RoboDAO;
import Business.Armazem.IRobo;
import Util.Coordenadas;
import Util.ResultadosMovimentoRobos;
import Util.Tuple;

import java.util.*;

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

    public void transmiteInfoRota(int idPalete, int idRobo, List<Coordenadas> percurso){
        Robo robo = roboDAO.get(idRobo);
        robo.setIdPalete(idPalete);
        roboDAO.put(idRobo,robo);
        this.rotas.put(idRobo,percurso);
    }

    public ResultadosMovimentoRobos moveRobos(){
        ResultadosMovimentoRobos resultadosMovimentoRobos = new ResultadosMovimentoRobos();
        Set<Integer> idsRobosEmTransito = rotas.keySet();
        Map<Integer,Robo> robosEmTransito = roboDAO.getRobos(idsRobosEmTransito);

        for(Map.Entry<Integer,Robo> r : robosEmTransito.entrySet()){
            Robo robo = r.getValue();
            Coordenadas proximoPasso = rotas.get(robo.getId()).remove(0);
            robo.setCoordenadas(proximoPasso);

            if(rotas.get(robo.getId()).isEmpty()){
                int idPalete = robo.getIdPalete();

                 if(idPalete!=0){ // está a ir buscar/transportar palete
                     int idZona = robo.getIdPrateleira();

                     if(idZona < 10){
                         resultadosMovimentoRobos.addPaleteRecolhida(idPalete,robo.getId(),robo.getCoordenadas());
                     }else{
                         resultadosMovimentoRobos.addTuploPaleteArmazenadaPrateleira(idPalete,idZona);
                         resultadosMovimentoRobos.addRobosQueArmazenaram(robo.getId(),
                                 robo.getZonaEstacionamento(),robo.getCoordenadas());

                         robo.setIdPalete(0);
                         robo.setIdPrateleira(0);
                     }
                 }
                rotas.remove(robo.getId());
            }
        }
        roboDAO.atualizaRobos(robosEmTransito);
        return resultadosMovimentoRobos;
    }
}
