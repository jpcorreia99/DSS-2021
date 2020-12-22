package Util;

public enum EstadoPalete {
    RECEM_CHEGADA(1),
    EM_LEVANTAMENTO(2),
    TRANSPORTE(3),
    ARMAZENADA(4);

    private final int valor;

    EstadoPalete(int valor)
    {
        this.valor = valor;
    }

    public int getValor(){
        return this.valor;
    }

    public static EstadoPalete getEnumByValor(int valor){
        for(EstadoPalete e : EstadoPalete.values()){
            if(e.valor==valor) return e;
        }
        return null;
    }
}
