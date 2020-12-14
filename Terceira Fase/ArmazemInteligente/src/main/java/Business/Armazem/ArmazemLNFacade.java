package Business.Armazem;

import Business.Armazem.Gestor.GestorFacade;
import Business.Armazem.Robo.RoboFacade;
import Business.Armazem.Stock.Palete;
import Business.Armazem.Stock.StockFacade;
import Business.IArmazemLN;

import Util.Coordenadas;
import Util.LeitorCodigosQR;
import Util.ResultadosMovimentoRobos;
import Util.Tuple;

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
    LeitorCodigosQR leitorCodigosQR;
    Boolean funciona = true;
    Mapa mapa;

    public ArmazemLNFacade () {
        roboFacade = new RoboFacade();
        stockFacade = new StockFacade();
        gestorFacade = new GestorFacade();
        mapa = new Mapa();

        try {
            this.leitorCodigosQR = new LeitorCodigosQR(lockPaletes, conditionNovaPalete);
            Thread threadLeitorCodigosQR = new Thread(this.leitorCodigosQR);
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
        while(funciona){
            try {
                lockPaletes.lock();
                System.out.println("Boy estou a tentar escalonar");
                while (listPaletes.isEmpty() || !roboFacade.existeRoboDisponivel()) {
                    conditionNovaPalete.await();
                }
                System.out.println("ESTOU A ESCALONAR OMG!!!");
                Palete palete = listPaletes.remove(0);
                int idPalete = stockFacade.encontraPrateleiraLivre();
                if(idPalete == 0){
                    System.out.println("BIG POOPOO\n\nBIG POOPOO");
                }

                int idRobo = roboFacade.encontraRoboLivre(); // falta implementar, deve marcar o robo como tendo uma palte
                List<Coordenadas> percurso3 = new ArrayList<>();
                Coordenadas c1 = new Coordenadas(1,1);
                Coordenadas c2 = new Coordenadas(2,2);
                Coordenadas c3 = new Coordenadas(3,3);
                Coordenadas c4 = new Coordenadas(6,6);
                percurso3.add(c1);
                percurso3.add(c2);
                percurso3.add(c3);
                percurso3.add(c4);
                roboFacade.transmiteInfoRota(palete.getId(),idRobo,percurso3);
                lockPaletes.unlock();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void moveRobos(){
        while(funciona) {
            ResultadosMovimentoRobos resultadosMovimentoRobos = roboFacade.moveRobos();
            // FODA-SE TEM AINDA DE ASSINALAR QUE O ROBO AGORA NÃO TEM PALETE !!! (dentro do metodo moveRobos prob)
            List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasPrateleiras = resultadosMovimentoRobos.getTuplosPaletesArmazenadasPrateleiras();
            stockFacade.assinalaPaletesArmazenadas(tuplosPaletesArmazenadasPrateleiras);
            Map<Integer, Tuple<Integer, Coordenadas>> mapPaleteRobo = resultadosMovimentoRobos.getPaletesRecolhidas();

        }
    }

    
    public int[][] getMapa () {
        return mapa.getMapa();
    }

    public void desligaSistema(){
        this.leitorCodigosQR.desliga();
        this.funciona=false;
    }
}
