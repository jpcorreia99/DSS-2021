package Util;

public enum Estado {
    ESPERA(1),
    TRANSPORTE(2),
    ARMAZENADA(3);

    private final int valor;

    private Estado(int valor)
    {
        this.valor = valor;
    }

    public int getValor(){
        return this.valor;
    }
}
