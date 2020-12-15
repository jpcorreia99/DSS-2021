package Business.Armazem;

import Business.Armazem.Gestor.GestorFacade;
import Business.Armazem.Robo.RoboFacade;
import Business.Armazem.Stock.Palete;
import Business.Armazem.Stock.StockFacade;
import Business.IArmazemLN;

import Util.*;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ArmazemLNFacade implements IArmazemLN {
    RoboFacade roboFacade;
    StockFacade stockFacade;
    GestorFacade gestorFacade;
    Lock lockPaletes = new ReentrantLock();
    Condition conditionNovaPaleteNovoRobo = lockPaletes.newCondition();
    LeitorCodigosQR leitorCodigosQR;
    Boolean funciona = true;
    Mapa mapa;

    public ArmazemLNFacade () {
        roboFacade = new RoboFacade();
        stockFacade = new StockFacade();
        gestorFacade = new GestorFacade();
        mapa = new Mapa();

        try {
            this.leitorCodigosQR = new LeitorCodigosQR(lockPaletes, conditionNovaPaleteNovoRobo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        Thread threadLeitorCodigosQR = new Thread(this.leitorCodigosQR);
        threadLeitorCodigosQR.start();

        Thread threadEscalonamentoRobos = new Thread(this::gereRobos);
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

    private void gereRobos(){
        Scanner sc = new Scanner(System.in);
        while(funciona){
            long start2 = System.currentTimeMillis();
            escalonaRobos();
            moveRobos();
            long finish2 = System.currentTimeMillis();
            long timeElapsed2 = finish2 - start2;
            System.out.println(timeElapsed2);
            sc.nextLine();
        }
    }

    private void escalonaRobos(){
        System.out.println("Boy estou a tentar escalonar");
        Integer idPalete;
        if((idPalete =stockFacade.getPaleteRecemChegada())!=null && roboFacade.existeRoboDisponivel()){
            stockFacade.marcaPaleteEmLevantamento(idPalete);
            int idPrateleira = stockFacade.encontraPrateleiraLivre();
            System.out.println("ESTOU A ESCALONAR OMG!!!, idPalete=" + idPalete + ",idPrateleira" + idPrateleira);
            if (idPrateleira == 0) {
                System.out.println("big poopoo");
            }

            Tuple<Integer, Coordenadas> tuploIdCoordenadas =
                    roboFacade.encontraRoboLivre(idPalete); // falta implementar, deve marcar o robo como tendo uma palte
            List<Coordenadas> percursoInicial = new ArrayList<>();
            percursoInicial.add(new Coordenadas(tuploIdCoordenadas.getT().getX() - 1, tuploIdCoordenadas.getT().getY()));

            roboFacade.transmiteInfoRota(idPalete, idPrateleira, tuploIdCoordenadas.getO(), percursoInicial, EstadoRobo.RECOLHA);
        }else{
            if(idPalete==null) {
                System.out.println("Não há paletes");
            }else{
                System.out.println("Não há robos");
            }
        }
    }

    private void moveRobos(){
            System.out.println(".");
            ResultadosMovimentoRobos resultadosMovimentoRobos = roboFacade.moveRobos();
            // processamento da recolha de paletes
            Map<Tuple<Integer,Integer>, Tuple<Integer, Coordenadas>> mapPaleteInfoRoboInfo =
                    resultadosMovimentoRobos.getPaletesRecolhidas();

            for(Map.Entry<Tuple<Integer,Integer>,Tuple<Integer,Coordenadas>> entradaPaleteRobo: mapPaleteInfoRoboInfo.entrySet()){
                Tuple<Integer,Integer> tuploPaletePrateleira = entradaPaleteRobo.getKey();
                Tuple <Integer,Coordenadas> tuploRoboCoordenadas = entradaPaleteRobo.getValue();
                int idPalete = tuploPaletePrateleira.getO();
                int idPrateleira = tuploPaletePrateleira.getT();
                int idRobo = tuploRoboCoordenadas.getO();
                Coordenadas coordenadasRobo = tuploRoboCoordenadas.getT();
                System.out.println("Robo que recolheu: "+idPalete+","+idPrateleira+","+idRobo+","+coordenadasRobo.toString());

                stockFacade.assinalaPaleteEmTransporte(idPalete);
                List<Coordenadas> trajetoAtePrateleira = this.mapa.calculaTrajeto(idPrateleira, coordenadasRobo);
                roboFacade.transmiteInfoRota(idPalete, idPrateleira,idRobo, trajetoAtePrateleira, EstadoRobo.TRANSPORTE);
            }

            // processamento da entrega de paletes
            List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasPrateleiras =
                    resultadosMovimentoRobos.getTuplosPaletesArmazenadasPrateleiras();
            stockFacade.assinalaPaletesArmazenadas(tuplosPaletesArmazenadasPrateleiras);

            // processamento dos robos que entregaram as paletes
            Map<Integer, Tuple<Integer, Coordenadas>> infoRobosQueArmazenaram =
                    resultadosMovimentoRobos.getInfoRobosQueArmazenaram();

            for(Map.Entry<Integer, Tuple<Integer,Coordenadas>> infoRoboQueArmazenou : infoRobosQueArmazenaram.entrySet()){
                int idRobo = infoRoboQueArmazenou.getKey();
                int idEstacionamento = infoRoboQueArmazenou.getValue().getO();
                Coordenadas coordenadasRobo = infoRoboQueArmazenou.getValue().getT();

                List<Coordenadas> trajetoAteEstacionamento = this.mapa.calculaTrajeto(idEstacionamento, coordenadasRobo);
                roboFacade.transmiteInfoRota(0,0,idRobo,trajetoAteEstacionamento,EstadoRobo.RETORNO);
            }
            // quando um robo termina o trajeto deve dar signal no lock e deve alterar o seu idDestino
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored){}
    }
    
    public int[][] getMapa () {
        return mapa.getMapa();
    }

    public void desligaSistema(){
        this.leitorCodigosQR.desliga();
        this.funciona=false;
    }
}
