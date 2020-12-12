package Model.Armazem.Stock;

public class Palete {
    private static int numeroPaletes = 0;
    private int id;
    private String material;

    public static void atualizaNumeroPaletes(int n) {
        numeroPaletes = n;
    }
    // construtor de uma nova palete
    public Palete(String material) {
        numeroPaletes++;
        this.id = numeroPaletes;
        this.material = material;
    }

    // construtor quando consultado da BD
    public Palete(int id, String material) {
        this.id = id;
        this.material = material;
    }

}
