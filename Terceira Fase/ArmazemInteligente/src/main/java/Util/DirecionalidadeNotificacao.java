package Util;

public enum DirecionalidadeNotificacao {
    PARA_ROBO(1),
    PARA_SERVIDOR(2);


    private final int valor;

    DirecionalidadeNotificacao(int valor)
    {
        this.valor = valor;
    }

    public int getValor(){
        return this.valor;
    }

    public static DirecionalidadeNotificacao getEnumByValor(int valor){
        for(DirecionalidadeNotificacao e : DirecionalidadeNotificacao.values()){
            if(e.valor==valor) return e;
        }
        return null;
    }
}
