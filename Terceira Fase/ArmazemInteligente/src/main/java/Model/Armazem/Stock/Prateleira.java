package Model.Armazem.Stock;

import Database.PaleteDAO;

public class Prateleira {
    private PaleteDAO paleteDAO;
    private int paleteId;

    public Prateleira(int paleteId) {
        this.paleteDAO = PaleteDAO.getInstance();
        this.paleteId = paleteId;
    }
}
