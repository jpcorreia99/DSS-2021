package Model.Armazem.Robo;

import Util.Coordenadas;
import Util.Coordenadas;

public class Robo {
    private int id;
    //coordenadas atiais do robo;
    private Coordenadas coordenadas;
    // prateleira a que se encontra destinado
    private int idPrateleira;
    // palete que carrega
    private int idPalete;

    public Robo(int id, int x, int y, int idPrateleira,int idPalete) {
        this.id = id;
        this.coordenadas = new Coordenadas(x,y);
        this.idPrateleira = idPrateleira;
        this.idPalete = idPalete;
    }

    public int getId() {
        return id;
    }

    public Coordenadas getCoordenadas() {
        return this.coordenadas.clone();
    }

    public void setCoordenadas(Coordenadas coordenadas) {
        this.coordenadas= coordenadas;
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
                ", coordenadas=(" + coordenadas.getX() + ","+coordenadas.getY()+")"+
                ", idPrateleira=" + idPrateleira +
                ", idPalete=" + idPalete +
                '}';
    }
}
