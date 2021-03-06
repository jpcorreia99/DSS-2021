package Business.Armazem;

import Util.Coordenadas;

import java.util.*;
import java.util.stream.Collectors;


public class Mapa {
    int[][] mapa;
    Map <Integer, Coordenadas> zonas;
    
    public Mapa () {
        zonas = new HashMap <>();

        zonas.put(0, new Coordenadas(1,1));
        
        for (int i = 0; i < 8; i++) {
            zonas.put(10+6*i, new Coordenadas(5+i,7));
            zonas.put(11+6*i, new Coordenadas(5+i,4));
            zonas.put(12+6*i, new Coordenadas(5+i,3));
            zonas.put(13+6*i, new Coordenadas(5+i,8));
            zonas.put(14+6*i, new Coordenadas(5+i,0));
            zonas.put(15+6*i, new Coordenadas(5+i,11));
        }
        
        for (int i = 1; i < 4; i++)
            zonas.put(i, new Coordenadas(2, i+3));
        
        /*
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
        */
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
    }
    public Coordenadas getCoordsZonaLevantamento(){
        return zonas.get(0).clone();
    }

    public Util.Coordenadas getCoords (int p) {
        return zonas.get(p).clone();
    }

    public int[][] getMapa() {
            return Arrays.stream(mapa).map(int[]::clone).toArray(int[][]::new);
    }

    public Util.Coordenadas getCoordChegadaZona(int id) {
        Coordenadas temp = zonas.get(id).clone();
        if(id > 9) {
            if (temp.getY() == 0 || temp.getY() == 4 || temp.getY() == 8)
                temp.addY(1);
            else temp.addY(-1);
        }
        return temp;
    }

    /**
     * Calcula uma rota mais curta desde as coordenadas de inicio até às coordenadas do fim
     * @param idZona Zona destino
     * @param inicio Coordenada de inicio
     * @return Lista de coordenadas a tomar para chegar ao seu destino
     */
    public List<Coordenadas> calculaRota(int idZona, Coordenadas inicio) {
        int [][] localMap = Arrays.stream(mapa).map(int[]::clone).toArray(int[][]::new);
        Coordenadas fim = this.getCoordChegadaZona(idZona);
        localMap = preencheMapa(inicio, fim, localMap);
        return getRota(inicio, localMap);
    }

    /**
     * Calcula a rota tendo como base o mapa de distâncias calculado anteriormente
     * @param inicio Coordenada de inicio
     * @param localMap Mapa de distancias
     * @return Lista de Coordenadas originada
     */
    private List<Coordenadas> getRota(Coordenadas inicio, int[][] localMap) {
        List<Coordenadas> finalList = new ArrayList<>();
        Coordenadas curPos = inicio.clone();
        int vetX, vetY, passoAtual, nextStep;
        boolean chosen;
        while((passoAtual = localMap[curPos.getY()][curPos.getX()]) != 0) {
            chosen = false;
            for (vetX = 1; vetX >= -1 && !chosen; vetX--)
                for (vetY = -1; vetY <= 1 && !chosen; vetY++)
                    if ((vetX == 0 && vetY != 0) || (vetX != 0 && vetY == 0))
                        if ((nextStep = localMap[curPos.getY() + vetY][curPos.getX() + vetX]) >= 0 && nextStep < passoAtual) {
                            chosen = true;
                            curPos.addY(vetY);
                            curPos.addX(vetX);
                            finalList.add(curPos.clone());
                        }
        }
        return finalList.stream().filter(c -> c.getX()!=1 && c.getX()!=0).collect(Collectors.toList());
    }

    /**
     * Constroi um mapa de distâncias até ao ponto inicial
     * @param inicio Coordenadas iniciais
     * @param fim Coordenadas finais
     * @param localMap Mapa de posições
     * @return Mapa de distâncias
     */
    private int[][] preencheMapa(Coordenadas inicio, Coordenadas fim, int[][] localMap) {
        int [][] finalMap;
        finalMap = convertMap(localMap);
        int distance = 1;
        int vetX, vetY;
        List<Coordenadas> waiting = new ArrayList<>();
        List<Coordenadas> nextWaiting = new ArrayList<>();
        waiting.add(fim.clone());
        finalMap[fim.getY()][fim.getX()] = 0;
        while(!waiting.contains(inicio)) {
            for(Coordenadas temp : waiting)
                for (vetX = -1; vetX <= 1; vetX++)
                    for (vetY = -1; vetY <= 1; vetY++)
                        if ((vetX == 0 && vetY != 0) || (vetX != 0 && vetY == 0))
                            if (distance < finalMap[temp.getY() + vetY][temp.getX() + vetX]) {
                                finalMap[temp.getY() + vetY][temp.getX() + vetX] = distance;
                                nextWaiting.add(new Coordenadas(temp.getX() + vetX, temp.getY() + vetY));
                            }
            distance++;
            waiting = nextWaiting;
            nextWaiting = new ArrayList<>();
        }
        return finalMap;
    }

    /**
     * Converte o mapa em questão para um mapa contendo apenas elementos com valor -1 e 99 em que toma como as posições com 99 navegáveis e as posições -1 inavegáveis
     * @param localMap Mapa em questão
     * @return Mapa de posições navegáveis
     */
    private int[][] convertMap(int[][] localMap) {
        int [][] res = new int[localMap.length][localMap[0].length];
        for(int i = 0; i < localMap.length; i++)
            for(int j = 0; j < localMap[0].length; j++) {
                if(localMap[i][j] == 0)
                    res[i][j] = 99;
                else if(localMap[i][j] != 0)
                    res[i][j] = -1;
            }
        return res;
    }
}
