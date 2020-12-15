package Business.Armazem;

import Util.Coordenadas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Mapa {
    int[][] mapa;
    Map <Integer, Coordenadas> zonas;
    
    public Mapa () {
        zonas = new HashMap <>();
        
        for (int i = 1; i < 5; i++)
            zonas.put(i, new Coordenadas(i+3, 2));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+10, new Coordenadas(0, i+5));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+18, new Coordenadas(3, i+5));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+26, new Coordenadas(4, i+5));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+34, new Coordenadas(7, i+5));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+42, new Coordenadas(8, i+5));
        
        for (int i = 0; i < 8; i++)
            zonas.put(i+50, new Coordenadas(11, i+5));
        
        mapa = new int[12][16];
        
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 16; j++) {    
                switch(i) {
                    case 0:
                    case 11:
                        if (j == 0 || j == 1)
                            mapa[i][j] = 0;
                        else if (j == 2 || j == 3 || j == 4 || j == 13 || j == 14 || j == 15)
                            mapa[i][j] = 1;
                        else mapa[i][j] = 2;
                        break;
                    case 1:
                    case 10:
                        if (j == 2 || 15 == j)
                            mapa[i][j] = 1;
                        else mapa[i][j] = 0;
                        break;
                    case 2:
                    case 9:
                        if (j != 0 && j != 1 && j != 2 && j != 15)
                            mapa[i][j] = 0;
                        else mapa[i][j] = 1;
                        break;
                    case 5:
                    case 6:
                        if (j == 0 || j == 15)
                            mapa[i][j] = 1;
                        else mapa[i][j] = 0;
                        break;
                    default:
                        if (j == 0 || j == 15)
                            mapa[i][j] = 1;
                        else if (j > 4 && j < 13)
                            mapa[i][j] = 2;
                        else mapa[i][j] = 0;
                        break;     
                }
            }
        }
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
    
    public int[][] getMapa() {
            return this.mapa.clone();
    }

    public List<Coordenadas> calculaTrajeto(int idZona, Coordenadas coordenadasAtuais) {
        List<Coordenadas> percurso3 = new ArrayList<>();
        Coordenadas c1 = new Coordenadas(1,1);
        Coordenadas c2 = new Coordenadas(2,2);
        Coordenadas c3 = new Coordenadas(3,3);
        Coordenadas c4 = new Coordenadas(6,6);
        percurso3.add(c1);
        percurso3.add(c2);
        percurso3.add(c3);
        percurso3.add(c4);
        return percurso3;
    }
}
