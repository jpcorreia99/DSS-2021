package Util;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordenadas)) return false;
        Coordenadas that = (Coordenadas) o;
        return getX() == that.getX() &&
                getY() == that.getY();
    }

    public String toString() {
        return "("+x+","+y+")";
    }

    public Coordenadas clone(){
        return new Coordenadas(x,y);
    }

}
