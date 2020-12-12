package Model.Armazem.Robo;

import Model.Armazem.IRobo;
import Util.Etapa;

import java.util.List;

public class RoboFacade implements IRobo{
    public boolean existeRoboDisponivel(){
        return true;
    }

    public int encontraRoboLivre(){
        return 1;
    }

    public void transmiteInfoRota(int idPalete, int idRobo, List<Etapa> percurso){

    }
}
