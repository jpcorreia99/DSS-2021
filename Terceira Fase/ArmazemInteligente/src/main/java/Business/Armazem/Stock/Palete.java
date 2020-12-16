package Business.Armazem.Stock;

public class Palete {
    private static int numeroPaletes = 0;
    private int id;
    private String material;
    private EstadoPalete estadoPalete;

    public static void atualizaNumeroPaletes(int n) {
        numeroPaletes = n;
    }
    // construtor de uma nova palete
    public Palete(String material) {
        numeroPaletes++;
        this.id = numeroPaletes;
        this.material = material;
        this.estadoPalete = EstadoPalete.RECEM_CHEGADA;

    }

    // construtor quando consultado da BD
    public Palete(int id, String material, EstadoPalete estadoPalete) {
        this.id = id;
        this.material = material;
        this.estadoPalete = estadoPalete;
    }

    public int getId() {
        return id;
    }

    public String getMaterial() {
        return material;
    }

    public EstadoPalete getEstado() {
        return estadoPalete;
    }

    public void setEstado(EstadoPalete estadoPalete) { this.estadoPalete = estadoPalete;}
}
