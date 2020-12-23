package Business;

import Util.Coordenadas;
import Util.EstadoPalete;
import Util.Tuple;

import java.util.Map;

public interface IArmazemLN {
    void start();

    /**
     * Devolve um conjunto de informação relativa a cada palete existente no sistema
     * @return Map em que a chave é o id de uma palete e o valor é uma estrutura composta por:
     * tuplo composto pelo material da palete e um segundo tuplo
     * composto pelo estado da palete e as suas coordenadas
     */
    Map<Integer,Tuple<String, Tuple<EstadoPalete, Coordenadas>>> getPaletes();

    boolean login (String user, String password);

    int[][] getMapa ();

    void desligaSistema();
}
