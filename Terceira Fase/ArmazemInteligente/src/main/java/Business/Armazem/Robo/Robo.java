package Business.Armazem.Robo;

import Database.PaleteDAO;
import Util.Coordenadas;

/**
 * Representação do robo na lógica de negócio
 */
public class Robo {
    private int id;
    //coordenadas atiais do robo;
    private Coordenadas coordenadas;
    //id da zona onde o robo estaciona
    private final int zonaEstacionamento;
    // prateleira a que se encontra destinado
    private int idPrateleira;
    // palete que carrega
    private int idPalete;
    // estado do robo
    private EstadoRobo estado;

    PaleteDAO paleteDAO;

    public Robo(int id, int x, int y, int zonaEstacionamento ,int idPrateleira,int idPalete, EstadoRobo estado) {
        this.id = id;
        this.coordenadas = new Coordenadas(x,y);
        this.zonaEstacionamento = zonaEstacionamento;
        this.idPrateleira = idPrateleira;
        this.idPalete = idPalete;
        this.estado = estado;
        this.paleteDAO = PaleteDAO.getInstance();
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

    public int getZonaEstacionamento() { return zonaEstacionamento; }

    public EstadoRobo getEstado() { return estado; }


    public void setIdPrateleira(int idPrateleira) {
        this.idPrateleira = idPrateleira;
    }

    public void setIdPalete(int idPalete) {
        this.idPalete = idPalete;
    }

    public void setEstado(EstadoRobo estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Robo{" +
                "id=" + id +
                ", coordenadas=" + coordenadas +
                ", zonaEstacionamento=" + zonaEstacionamento +
                ", idPrateleira=" + idPrateleira +
                ", idPalete=" + idPalete +
                ", estado=" + estado +
                '}';
    }
}
