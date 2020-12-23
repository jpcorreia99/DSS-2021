package Business.Armazem;

import Util.Coordenadas;
import Util.EstadoRobo;
import Util.ResultadosMovimentoRobos;
import Util.Tuple;

import java.util.List;

public interface IRobo {
    boolean existemRobosDisponiveis();

    Tuple<Integer,Coordenadas> encontraRoboLivre(int idPalete);

    void transmiteInfoRota(int idPalete, int idPrateleira , int idRobo, List<Coordenadas> percurso, EstadoRobo estadoATer);

    ResultadosMovimentoRobos recolheNotificacoes();

    Coordenadas getCoordPalete(int idPalete);

    /**
     * Devolve uma lista com um tuplo contendo as coordenadas do robo e um booleano
     * que indica se ele está a ir entregar a palete ou não
     * @return
     */
    List<Tuple<Coordenadas, Boolean>> getInfoRobos();
}
