package Business;

import Util.Coordenadas;
import Util.EstadoPalete;
import Util.Tuple;

import java.util.Map;

public interface IArmazemLN {
    void start();
    Map<Integer,Tuple<String, Tuple<EstadoPalete, Coordenadas>>> getPaletes();
    boolean login (String user, String password);
    int[][] getMapa ();
    void desligaSistema();
}
