package Util;

public class Coordenadas {
    private int x;
    private int y;

    public Coordenadas(int x, int y){
        this.x=x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordenadas clone(){
        return new Coordenadas(x,y);
    }

    @Override
    public String toString() {
        return "Coordenadas{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
