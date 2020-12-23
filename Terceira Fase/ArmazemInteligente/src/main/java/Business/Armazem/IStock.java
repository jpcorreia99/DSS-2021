package Business.Armazem;

import Util.EstadoPalete;
import Util.Tuple;

import java.util.List;
import java.util.Map;

public interface IStock {
    boolean armazemTemEspacoDisponivel();

    boolean existemPaletesRecemChegadas();

    int getPaleteRecemChegada();

    void marcaPaleteEmLevantamento(int idPalete);

    void assinalaPaleteEmTransporte(int idPalete);

    void assinalaPaletesArmazenadas(List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasPrateleiras);

    int encontraPrateleiraLivre();

    Map<Integer,Tuple<String, EstadoPalete>> getLocPaletes();

    int getIdPrateleiraGuardaPalete(int idPalete);

    List<Integer> getPrateleirasOcupadas();
}
