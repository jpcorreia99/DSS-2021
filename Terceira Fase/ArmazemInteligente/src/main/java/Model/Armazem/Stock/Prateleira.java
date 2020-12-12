package Model.Armazem.Stock;

public class Prateleira {
    private int id;
    private boolean estaOcupada;
    private int idPalete;
    private static int idCounter = 0;

    /**
     * Contrutor por defeito
     */
    public Prateleira() {
        idCounter++;
        id = idCounter;
        estaOcupada = false;
        idPalete = 0;
    }

    /**
     * Contrutor parametrizado
     * @param id id da prateleira
     * @param estaOcupada bool se está ocupada
     * @param idPalete 0 se contém palete ou o id desta se existir
     */
    public Prateleira(int id, boolean estaOcupada, int idPalete) {
        this.id = id;
        this.idPalete = idPalete;
        this.estaOcupada = estaOcupada;
    }

    /**
     * Contrutor por cópia
     * @param prat Objeto a ser copiado
     */
    public Prateleira(Prateleira prat) {
        setId(prat.getId());
        setEstaOcupada(prat.isEstaOcupada());
        setIdPalete(prat.getIdPalete());
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
     * Obtém o valor se está ocupada ou não
     * @return true se está ocupada, false caso contrario
     */
    public boolean isEstaOcupada() {
        return estaOcupada;
    }

    /**
     * Atualiza o valor se está ocupada ou não
     * @param estaOcupada novo booleano
     */
    public void setEstaOcupada(boolean estaOcupada) {
        this.estaOcupada = estaOcupada;
    }

    /**
     * Obtém o id da palete contida
     * @return 0 se não houver palete ou o id desta se existir
     */
    public int getIdPalete() {
        return idPalete;
    }

    /**
     * Atualzia o id da palete contida
     * @param idPalete novo id.
     */
    public void setIdPalete(int idPalete) {
        this.idPalete = idPalete;
    }
}
