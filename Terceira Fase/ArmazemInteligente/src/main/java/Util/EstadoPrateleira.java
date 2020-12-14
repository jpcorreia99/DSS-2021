package Util;

public enum EstadoPrateleira {
    LIVRE(1),
    COMPROMETIDA(2),
    OCUPADA(3);

    private final int valor;

    private EstadoPrateleira(int valor)
    {
        this.valor = valor;
    }

    public int getValor(){
        return this.valor;
    }

    public static EstadoPrateleira getEnumByValor(int valor){
        for(EstadoPrateleira e : EstadoPrateleira.values()){
            if(e.valor==valor) return e;
        }
        return null;
    }
}
