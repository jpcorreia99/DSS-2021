package Util;

public class Tuple<o,t> {

    private o one;
    private t two;

    public Tuple(o one, t two) {
        this.one = one;
        this.two = two;
    }

    public o getO() {
        return one;
    }

    public t getT() {
        return two;
    }
}