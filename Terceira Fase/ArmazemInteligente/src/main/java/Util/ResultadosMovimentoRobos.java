package Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultadosMovimentoRobos {
    // tuplo idPalete, idPrateleira
    private List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasPrateleiras;

    private Map<Integer, Tuple<Integer,Coordenadas>> paletesRecolhidas; // (idPalete, idRobo,coordenadasRobo)

    /* idRobo -> (idEstacionamento, coordenadasAtuais) */
    private Map<Integer, Tuple<Integer, Coordenadas>> robosQueArmazenaram;

    public ResultadosMovimentoRobos() {
        this.tuplosPaletesArmazenadasPrateleiras = new ArrayList<>();
        this.paletesRecolhidas = new HashMap<>();
        this.robosQueArmazenaram = new HashMap<>();
    }
    public void addTuploPaleteArmazenadaPrateleira(int idPalete, int idZona){
        this.tuplosPaletesArmazenadasPrateleiras.add(new Tuple<Integer,Integer>(idPalete,idZona));
    }

    public void addPaleteRecolhida(int idPalete, int idRobo, Coordenadas coordenadasRobo){
        Tuple<Integer,Coordenadas> tuple = new Tuple<>(idRobo,coordenadasRobo);
        this.paletesRecolhidas.put(idPalete,tuple);
    }

    public void addRobosQueArmazenaram(int idRobo, int idEstacionamento, Coordenadas coordenadasRobo){
        Tuple<Integer,Coordenadas> tuple = new Tuple<>(idEstacionamento,coordenadasRobo);
        this.robosQueArmazenaram.put(idRobo,tuple);
    }

    public List<Tuple<Integer, Integer>> getTuplosPaletesArmazenadasPrateleiras() {
        return tuplosPaletesArmazenadasPrateleiras;
    }

    public Map<Integer, Tuple<Integer, Coordenadas>> getPaletesRecolhidas() {
        return paletesRecolhidas;
    }

    public Map<Integer, Tuple<Integer, Coordenadas>> getRobosQueArmazenaram() {
        return robosQueArmazenaram;
    }
}
