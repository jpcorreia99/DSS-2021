package Business.Armazem;

import Util.Coordenadas;

import java.util.*;


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
    
    public Util.Coordenadas getCoords(int p) {
        return zonas.get(p).clone();
    }

    public int[][] getMapa() {
            return this.mapa.clone();
    }

    /**
     * Calcula uma rota mais curta desde as coordenadas de inicio até às coordenadas do fim
     * @param idZona zona destino
     * @param inicio Coordenada de inicio
     * @return Lista de coordenadas a tomar para chegar ao seu destino
     */
    public List<Coordenadas> calculaRota(int idZona, Coordenadas inicio) {
        int [][] localMap = mapa.clone();
        Coordenadas fim = this.zonas.get(idZona);
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
        List<Coordenadas> finalQueue = new LinkedList<>();
        Coordenadas curPos = inicio.clone();
        int vetX, vetY, passoAtual, nextStep;
        while((passoAtual = localMap[curPos.getY()][curPos.getX()]) != 0)
            for(vetX = -1; vetX <= 1; vetX++)
                for(vetY = -1; vetY <= 1; vetY++)
                    if ((vetX == 0 && vetY != 0) || (vetX != 0 && vetY == 0))
                        if((nextStep = localMap[curPos.getY() + vetY][curPos.getX() + vetX]) >= 0 && nextStep < passoAtual) {
                            curPos.addY(vetY);
                            curPos.addX(vetX);
                            finalQueue.add(curPos.clone());
                        }
        return finalQueue;
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
