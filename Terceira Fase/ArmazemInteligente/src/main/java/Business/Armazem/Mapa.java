package Business.Armazem;

import Util.Coordenadas;
import java.util.HashMap;
import java.util.Map;


public class Mapa {
    int[][] mapa;
    Map <Integer, Coordenadas> zonas;
    
    public Mapa () {
        zonas = new HashMap <>();
        
        for (int i = 1; i < 5; i++)
            zonas.put(i, new Coordenadas(2, i+3));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+10, new Coordenadas(i+5, 0));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+18, new Coordenadas(i+5, 3));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+26, new Coordenadas(i+5, 4));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+34, new Coordenadas(i+5, 7));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+42, new Coordenadas(i+5, 8));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+50, new Coordenadas(i+5, 11));
        
        mapa = new int[][] {
            {0,0,1,1,1,2,2,2,2,2,2,2,2,1,1,1},
            {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,2,2,2,2,2,2,2,2,0,0,1},
            {1,0,0,0,0,2,2,2,2,2,2,2,2,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,2,2,2,2,2,2,2,2,0,0,1},
            {1,0,0,0,0,2,2,2,2,2,2,2,2,0,0,1},
            {1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {0,0,1,1,1,2,2,2,2,2,2,2,2,1,1,1},
        };

        /* DEBUG
        for (int i = 0; i < 12 ; i++) {
            for (int j = 0; j < 16; j++) {
                System.out.print(mapa[i][j]);
            }
            System.out.println();
        }
        
        for (Map.Entry<Integer, Coordenadas> e : this.zonas.entrySet()) {
            Coordenadas c = e.getValue();
            System.out.println("A zona " + e.getKey() + " tem coordenadas " + "(" + c.getX() + "," + c.getY() + ")");
        }
        */
    }
    
    public Util.Coordenadas getCoords(int p) {
        return zonas.get(p).clone();
    }
    
    public int[][] getMapa() {
            return this.mapa.clone();
    }
}
