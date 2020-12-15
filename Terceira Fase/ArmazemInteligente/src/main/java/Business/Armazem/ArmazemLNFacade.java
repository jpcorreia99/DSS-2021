package Business.Armazem;

import Business.Armazem.Gestor.GestorFacade;
import Business.Armazem.Robo.RoboFacade;
import Business.Armazem.Stock.Palete;
import Business.Armazem.Stock.StockFacade;
import Business.IArmazemLN;

import Exceptions.ArmazemCheioException;
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

        Thread moveRobos = new Thread(this::moveRobos);
        moveRobos.start();
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
                Integer idPalete;
                while ((idPalete =stockFacade.getPaleteRecemChegada())==null || !roboFacade.existeRoboDisponivel()) {
                    System.out.println("estou no wait da condição mais de fora");
                    conditionNovaPalete.await();
                }

                int idPrateleira = stockFacade.encontraPrateleiraLivre();
                System.out.println("ESTOU A ESCALONAR OMG!!!, idPalete="+idPalete+",idPrateleira"+idPrateleira);
                if(idPrateleira==0) {
                   System.out.println("big poopoo");
                   break;
                }

                Tuple<Integer,Coordenadas> tuploIdCoordenadas=
                        roboFacade.encontraRoboLivre(idPalete); // falta implementar, deve marcar o robo como tendo uma palte
                List<Coordenadas> percursoInicial = new ArrayList<>();
                percursoInicial.add(new Coordenadas(tuploIdCoordenadas.getT().getX()-1,tuploIdCoordenadas.getT().getX()));

                roboFacade.transmiteInfoRota(idPalete, tuploIdCoordenadas.getO(), percursoInicial);

                lockPaletes.unlock();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void moveRobos(){
        while(funciona) {
            ResultadosMovimentoRobos resultadosMovimentoRobos = roboFacade.moveRobos();
//            System.out.println("Robos moveram-se");
            // FODA-SE TEM AINDA DE ASSINALAR QUE O ROBO AGORA NÃO TEM PALETE !!! (dentro do metodo moveRobos prob)
            List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasPrateleiras = resultadosMovimentoRobos.getTuplosPaletesArmazenadasPrateleiras();
            stockFacade.assinalaPaletesArmazenadas(tuplosPaletesArmazenadasPrateleiras);
            Map<Integer, Tuple<Integer, Coordenadas>> mapPaleteRobo = resultadosMovimentoRobos.getPaletesRecolhidas();

            // quando um robo termina o trajeto deve dar signal no lock
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored){}
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
