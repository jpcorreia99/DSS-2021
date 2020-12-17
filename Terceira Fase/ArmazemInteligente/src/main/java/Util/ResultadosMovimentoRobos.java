package Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultadosMovimentoRobos {
    /* informação relativa às paletes que foram recolhidas*/
    // (idPalete, idPrateleira) -> (idRobo,coordenadasRobo))
    private Map<Tuple<Integer, Integer>, Tuple<Integer,Coordenadas>> paletesRecolhidas;


    /* informação relativa às várias paletes que foram entregues*/
    // tuplo idPalete, idPrateleira
    private List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasPrateleiras;

    /* informação relativa aos robos que acabaram de entregar as paletes*/
    /* idRobo -> (idEstacionamento, coordenadasAtuais) */
    private Map<Integer, Tuple<Integer, Coordenadas>> infoRobosQueArmazenaram;

    public ResultadosMovimentoRobos() {
        this.tuplosPaletesArmazenadasPrateleiras = new ArrayList<>();
        this.paletesRecolhidas = new HashMap<>();
        this.infoRobosQueArmazenaram = new HashMap<>();
    }

    public void addPaleteRecolhida(int idPalete, int idPrateleira ,int idRobo, Coordenadas coordenadasRobo){
        Tuple<Integer,Integer> tuploPaletePrateleira = new Tuple<>(idPalete,idPrateleira);
        Tuple<Integer,Coordenadas> tuploRoboCoordenadas = new Tuple<>(idRobo,coordenadasRobo);
        this.paletesRecolhidas.put(tuploPaletePrateleira,tuploRoboCoordenadas);
    }

    public void addTuploPaleteArmazenadaPrateleira(int idPalete, int idZona){
        this.tuplosPaletesArmazenadasPrateleiras.add(new Tuple<Integer,Integer>(idPalete,idZona));
    }

    public void addRoboQueArmazenou(int idRobo, int idEstacionamento, Coordenadas coordenadasRobo){
        Tuple<Integer,Coordenadas> tuple = new Tuple<>(idEstacionamento,coordenadasRobo);
        this.infoRobosQueArmazenaram.put(idRobo,tuple);
    }


    public Map<Tuple<Integer,Integer>, Tuple<Integer, Coordenadas>> getPaletesRecolhidas() {
        return paletesRecolhidas;
    }

    public List<Tuple<Integer, Integer>> getTuplosPaletesArmazenadasPrateleiras() {
        return tuplosPaletesArmazenadasPrateleiras;
    }

    public Map<Integer, Tuple<Integer, Coordenadas>> getInfoRobosQueArmazenaram() {
        return infoRobosQueArmazenaram;
    }
}
