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

    public void addX(int amount) {
        this.x += amount;
    }

    public void addY(int amount) {
        this.y += amount;
    }

    public Coordenadas clone(){
        return new Coordenadas(x,y);
    }
}
