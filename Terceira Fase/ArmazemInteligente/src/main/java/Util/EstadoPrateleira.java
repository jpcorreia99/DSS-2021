package Util;

public enum EstadoPrateleira {
    LIVRE(1),
    COMPROMETIDA(2),
    OCUPADA(3);

    private final int valor;

    EstadoPrateleira(int valor)
    {
        this.valor = valor;
    }

    public int getValor(){
        return this.valor;
    }
}
