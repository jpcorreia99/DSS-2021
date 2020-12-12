package Model.Armazem.Stock;

import Database.PaleteDAO;

public class Prateleira {
    private int id;
    private PaleteDAO paleteDAO;
    private int paleteId;

    /**
     * Contrutor parametrizado
     * @param id id da prateleira
     * @param paleteId 0 se contém palete ou o id desta se existir
     */
    public Prateleira(int id,int paleteId) {
        this.id = id;
        this.paleteDAO = PaleteDAO.getInstance();
        this.paleteId = paleteId;
    }

    /**
     * Contrutor por cópia
     * @param prat Objeto a ser copiado
     */
    public Prateleira(Prateleira prat) {
        setId(prat.getId());
        setPaleteId(prat.getPaleteId());
    }

    /**
     * Obtém Id da prateleira.
     * @return Id da prateleira
     */
    public int getId() {
        return id;
    }

    /**
     * Atualiza id da prateleira
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Obtém o id da palete contida
     * @return 0 se não houver palete ou o id desta se existir
     */
    public int getPaleteId() {
        return paleteId;
    }

    /**
     * Atualzia o id da palete contida
     * @param paleteId novo id.
     */
    public void setPaleteId(int paleteId) {
        this.paleteId = paleteId;
    }
}
