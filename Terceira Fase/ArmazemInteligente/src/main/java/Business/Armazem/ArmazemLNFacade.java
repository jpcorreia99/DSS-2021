package Business.Armazem;

import Business.Armazem.Gestor.GestorFacade;
import Business.Armazem.Robo.RoboFacade;
import Business.Armazem.Stock.Palete;
import Business.Armazem.Stock.StockFacade;
import Business.IArmazemLN;

import Util.Coordenadas;
import Util.LeitorCodigosQR;

import java.io.IOException;

import java.util.ArrayList;
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
    
    public Map<Integer, Palete> getPaletes() {
         return new HashMap<>(); 
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
        return mapa.getMapa();
    }
}
