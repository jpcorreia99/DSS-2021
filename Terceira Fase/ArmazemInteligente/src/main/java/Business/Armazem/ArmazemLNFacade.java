package Business.Armazem;

import Business.Armazem.Gestor.GestorFacade;
import Business.Armazem.Robo.RoboFacade;
import Business.Armazem.Stock.Palete;
import Business.Armazem.Stock.StockFacade;
import Business.IArmazemLN;

import Util.Coordenadas;
import Util.LeitorCodigosQR;
import Util.Tuple;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ArmazemLNFacade implements IArmazemLN {
    RoboFacade roboFacade;
    StockFacade stockFacade;
    GestorFacade gestorFacade;
    List<Palete> listPaletes = new ArrayList<>();
    Lock lockPaletes = new ReentrantLock();
    Condition conditionNovaPalete = lockPaletes.newCondition();
    Mapa mapa;

    public ArmazemLNFacade () {
        roboFacade = new RoboFacade();
        stockFacade = new StockFacade();
        gestorFacade = new GestorFacade();
        mapa = new Mapa();

        try {
            Thread threadLeitorCodigosQR = new Thread(new LeitorCodigosQR(listPaletes, lockPaletes, conditionNovaPalete));
            threadLeitorCodigosQR.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread threadEscalonamentoRobos = new Thread(this::escalonaRobos);
        threadEscalonamentoRobos.start();

//        Thread moveRobos = new Thread(this::moveRobos);
//        moveRobos.start();
    }
    
    public Map<Integer, Tuple<String, Integer>> getPaletes() {
        Map<Integer, Tuple<String, Integer>> paletes = new HashMap<>();
         
        String[] materiais = {"Frangos", "Vibradores", "Tremoços", "Iogurtes", "Memes", "2ª fase de DSS", "Valérios", "Pinguins", "Esperanças", "Pantufas", "Caloiros"};
        Integer[] ys = {0, 7, 6, 4, 1, 8, 3, 2, 8, 2, 9};
        Integer[] xs = {7, 3, 3, 2, 9, 0, 0, 2, 3, 1, 11};
        Integer[] estado = {1, 2, 2, 3, 1, 4, 1, 3, 2, 4, 1};
        
        for (int i = 0; i < 10; i++)
            paletes.put(i+1, new Tuple(materiais[i], estado[i]));
         //return stockFacade.getLocPaletes();
         return paletes;
    }
    
    public boolean login (String user, String password) {
        return this.gestorFacade.login(user,password);
    }

    private void escalonaRobos(){
        while(true){
            try {
                lockPaletes.lock();
                System.out.println("Boy estou a tentar escalonar");
                while (listPaletes.isEmpty() || !roboFacade.existeRoboDisponivel()) {
                    conditionNovaPalete.await();
                }
                System.out.println("ESTOU A ESCALONAR OMG!!!");
                Palete palete = listPaletes.remove(0);
                int idRobo = roboFacade.encontraRoboLivre();
                List<Coordenadas> percurso = new ArrayList<>();
                roboFacade.transmiteInfoRota(palete.getId(),idRobo,percurso);
                lockPaletes.unlock();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

//    private void moveRobos(){
//        while(true) {
//            Tuple<ArrayList<Integer>, // ids de paletes em transporte
//                    ArrayList<Integer>> paletesAlterads = null;
//
//            // atualizar estado para em transporte
//            // atualizar estado para guardada
//        }
//    }

    
    public int[][] getMapa () {
        int[][] map = mapa.getMapa();
        //List<Integer> prateleiras = stockFacade.getPrateleirasOcupadas();
        //List<Tuple<Coordenadas, Boolean>> robos = roboFacade.getInfoRobo();
 
        List <Integer> prateleiras = Arrays.asList(new Integer[] {16, 24, 25, 32, 40, 41, 42});
        Tuple<Coordenadas, Boolean> robo1 = new Tuple(new Coordenadas(3,4), true);
        Tuple<Coordenadas, Boolean> robo2 = new Tuple(new Coordenadas(6,2), false);
        Tuple<Coordenadas, Boolean> robo3 = new Tuple(new Coordenadas(8,10), true);
        Tuple<Coordenadas, Boolean> robo4 = new Tuple(new Coordenadas(9,2), false);
        
        List<Tuple<Coordenadas, Boolean>> robos = new ArrayList<>();
        robos.add(robo1);
        robos.add(robo2);
        robos.add(robo3);
        robos.add(robo4);
        
        for (Integer i : prateleiras) {
            Coordenadas c = mapa.getCoords(i);
            map[c.getY()][c.getX()] = 3;
        }
        
        for (Tuple<Coordenadas, Boolean> t : robos) {
            Coordenadas c = t.getO();
            map[c.getY()][c.getX()] = t.getT() ? 5 : 4;
        }
        
        return map;
    }
}
