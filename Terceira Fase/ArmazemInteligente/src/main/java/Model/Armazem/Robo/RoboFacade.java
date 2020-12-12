package Model.Armazem.Robo;

import Database.RoboDAO;
import Model.Armazem.IRobo;
import Util.Coordenadas;
import Util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoboFacade implements IRobo{
    Map<Integer, ArrayList<Coordenadas>> rotas;
    RoboDAO roboDAO;

    public RoboFacade(){
        this.roboDAO = RoboDAO.getInstance();
        this.rotas = new HashMap<>();
    }

    public boolean existeRoboDisponivel(){
        return true;
    }

    public int encontraRoboLivre(){
        return 1;
    }

    public void transmiteInfoRota(int idPalete, int idRobo, List<Coordenadas> percurso){

    }

    public Tuple<List<Integer>,List<Integer>> moveRobos(){
        List<Integer> listaPaletesRecolhidas = new ArrayList<>();
        List<Integer> listaPaletesArmazenadas = new ArrayList<>();

        for(Map.Entry<Integer, ArrayList<Coordenadas>> rotaRobo : rotas.entrySet()){
            if(!rotaRobo.getValue().isEmpty()) {
                Robo robo = roboDAO.get(rotaRobo.getKey());
                Coordenadas proximoPasso = rotaRobo.getValue().remove(0);
                robo.setCoordenadas(proximoPasso);
                if (proximoPasso.getX() == 0 && proximoPasso.getY() == 0) {// robo chegou à zona de recolha
                    listaPaletesRecolhidas.add(robo.getIdPalete());
                }
                if (rotaRobo.getValue().size()==0){
                    listaPaletesArmazenadas.add(robo.getIdPalete());
                }
            }
        }
        return null;
    }
}
