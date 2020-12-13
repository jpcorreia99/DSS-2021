package Model.Armazem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mapa {
    Map <Integer, List <Integer>> mapa;
    
    public Mapa () {
        this.mapa = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            ArrayList <Integer> linha = new ArrayList<>();
            for (int j = 0; j < 16; j++) {    
                switch(i) {
                    case 0:
                    case 11:
                        if (j == 0 || j == 1)
                            linha.add(0);
                        else if (j == 2 || j == 3 || j == 4 || j == 13 || j == 14 || j == 15)
                            linha.add(1);
                        else linha.add(2);
                        break;
                    case 1:
                    case 10:
                        if (j == 2 || 15 == j)
                            linha.add(1);
                        else linha.add(0);
                        break;
                    case 2:
                    case 9:
                        if (j != 0 && j != 1 && j != 2 && j != 15)
                            linha.add(0);
                        else linha.add(1);
                        break;
                    case 5:
                    case 6:
                        if (j == 0 || j == 15)
                            linha.add(1);
                        else linha.add(0);
                        break;
                    default:
                        if (j == 0 || j == 15)
                            linha.add(1);
                        else if (j > 4 && j < 13 && (j+i) % 2 != 0)
                            linha.add(2);
                        else if (j > 4 && j < 13 && (j+i) % 2 == 0)
                            linha.add(3);
                        else linha.add(0);
                        break;     
                }
            }
            this.mapa.put(i, linha);
        }
        /*
        for (List<Integer> l : this.mapa.values()) {
            for (Integer k : l) {
                System.out.print(k);
            }
            System.out.println();
        }
        */
    }
    
    public Map <Integer, List<Integer>> getMapa() {
            Map <Integer, List <Integer>> map = this.mapa.entrySet() 
                  .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); 
            return map;
    }
}
