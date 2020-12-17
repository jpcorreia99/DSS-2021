package Business.Armazem.Robo;

public enum EstadoRobo {
    LIVRE(1),
    RECOLHA(2),
    TRANSPORTE(3),
    RETORNO(4);

    private final int valor;

    private EstadoRobo(int valor)
    {
        this.valor = valor;
    }

    public int getValor(){
        return this.valor;
    }

    public static EstadoRobo getEnumByValor(int valor){
        for(EstadoRobo e : EstadoRobo.values()){
            if(e.valor==valor) return e;
        }
        return null;
    }
}
