package Model.Armazem.Robo;

public class Robo {
    private int id;
    private int nodoAtual;
    private int idPrateleira;
    private int idPalete;

    public Robo(int id, int nodoAtual, int idPrateleira, int idPalete) {
        this.id = id;
        this.nodoAtual = nodoAtual;
        this.idPrateleira = idPrateleira;
        this.idPalete = idPalete;
    }

    public int getId() {
        return id;
    }

    public int getNodoAtual() {
        return nodoAtual;
    }

    public int getIdPrateleira() {
        return idPrateleira;
    }

    public int getIdPalete() {
        return idPalete;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNodoAtual(int nodoAtual) {
        this.nodoAtual = nodoAtual;
    }

    public void setIdPrateleira(int idPrateleira) {
        this.idPrateleira = idPrateleira;
    }

    public void setIdPalete(int idPalete) {
        this.idPalete = idPalete;
    }

    @Override
    public String toString() {
        return "Robo{" +
                "id=" + id +
                ", nodoAtual=" + nodoAtual +
                ", idPrateleira=" + idPrateleira +
                ", idPalete=" + idPalete +
                '}';
    }
}
