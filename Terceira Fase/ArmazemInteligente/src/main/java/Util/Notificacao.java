package Util;

public class Notificacao {
    private final int idRobo;
    private final TipoNotificacao tipo;
    private int id;
    private static int counter=0;

    public Notificacao(int idRobo, TipoNotificacao tipo) {
        counter++;
        this.id=counter;
        this.idRobo= idRobo;
        this.tipo = tipo;
    }

    public Notificacao(int id, int idRobo, TipoNotificacao tipo) {
        this.idRobo = idRobo;
        this.tipo = tipo;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getIdRobo() {
        return idRobo;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return "Notificacao{" +
                "idRobo=" + idRobo +
                ", tipo=" + tipo +
                ", id=" + id +
                '}';
    }
}
