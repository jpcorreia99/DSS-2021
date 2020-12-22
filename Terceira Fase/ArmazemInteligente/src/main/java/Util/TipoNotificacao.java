package Util;

public enum TipoNotificacao {
    RECOLHA(1),
    ENTREGA(2),
    NOVA_ROTA(3);


    private final int valor;

    TipoNotificacao(int valor)
    {
        this.valor = valor;
    }

    public int getValor(){
        return this.valor;
    }

    public static TipoNotificacao getEnumByValor(int valor){
        for(TipoNotificacao e : TipoNotificacao.values()){
            if(e.valor==valor) return e;
        }
        return null;
    }
}
