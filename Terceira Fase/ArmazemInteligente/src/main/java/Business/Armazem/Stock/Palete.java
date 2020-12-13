package Business.Armazem.Stock;

import Util.Estado;

public class Palete {
    private static int numeroPaletes = 0;
    private int id;
    private String material;
    private Estado estado;

    public static void atualizaNumeroPaletes(int n) {
        numeroPaletes = n;
    }
    // construtor de uma nova palete
    public Palete(String material) {
        numeroPaletes++;
        this.id = numeroPaletes;
        this.material = material;
        this.estado = Estado.ESPERA;

    }

    // construtor quando consultado da BD
    public Palete(int id, String material, Estado estado) {
        this.id = id;
        this.material = material;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getMaterial() {
        return material;
    }

    public Estado getEstado() {
        return estado;
    }
}
