package Business.Armazem.Stock;

import Database.PaleteDAO;
import Util.EstadoPrateleira;

public class Prateleira {
    private int id;
    /** Assinala se já existe uma palete que lhe foi atribuida (guardada ou a caminho*/
    private EstadoPrateleira estado;
    private PaleteDAO paleteDAO;
    private int idPalete;


    public Prateleira(int id, EstadoPrateleira estado, int idPalete) {
        this.id = id;
        this.estado = estado;
        this.paleteDAO = PaleteDAO.getInstance();
        this.idPalete = idPalete;
    }
}
