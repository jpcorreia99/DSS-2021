package Business.Armazem;

import Business.Armazem.Gestor.GestorFacade;
import Business.Armazem.Robo.EstadoRobo;
import Business.Armazem.Robo.RoboFacade;
import Business.Armazem.Stock.StockFacade;
import Business.IArmazemLN;

import Requests.LeitorCodigosQR;
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
    }

    public boolean login (String user, String password) {
        return this.gestorFacade.login(user,password);
    }

    private void gereRobos(){
        while(funciona){
            long start2 = System.currentTimeMillis();
            escalonaRobos();
            moveRobos();
            long finish2 = System.currentTimeMillis();
            long timeElapsed2 = finish2 - start2;
            System.out.println(timeElapsed2);
        }
    }

    private void escalonaRobos(){
        System.out.println("Boy estou a tentar escalonar");
        if(stockFacade.existemPaletesRecemChegadas() && roboFacade.existemRobosDisponiveis()){
            int idPalete =stockFacade.getPaleteRecemChegada();
            int idPrateleira = stockFacade.encontraPrateleiraLivre();
            System.out.println("ESTOU A ESCALONAR OMG!!!, idPalete=" + idPalete + ",idPrateleira" + idPrateleira);
            if (idPrateleira != 0) {
                stockFacade.marcaPaleteEmLevantamento(idPalete);
                Tuple<Integer, Coordenadas> tuploIdCoordenadas =
                        roboFacade.encontraRoboLivre(idPalete); // falta implementar, deve marcar o robo como tendo uma palte
                List<Coordenadas> percursoInicial = new ArrayList<>();
                percursoInicial.add(new Coordenadas(tuploIdCoordenadas.getT().getX() - 1, tuploIdCoordenadas.getT().getY()));

                roboFacade.transmiteInfoRota(idPalete, idPrateleira, tuploIdCoordenadas.getO(), percursoInicial, EstadoRobo.RECOLHA);
            }else {
                System.out.println("Big poopoo");
            }
        }else{
            if(!stockFacade.existemPaletesRecemChegadas()) {
                System.out.println("Não há paletes");
            }else{
                System.out.println("Não há robos");
            }
        }
    }

    /**
     * Função que moverá processará no sistema todas as alterações resultantes da movimentação de todos os robos
     * com rotas por 1 time step.
     */
    private void moveRobos(){
            System.out.println(".");
            ResultadosMovimentoRobos resultadosMovimentoRobos = roboFacade.moveRobos();
            // processamento da recolha de paletes
            processaRecolhaPaletes(resultadosMovimentoRobos.getPaletesRecolhidas());

            // processamento da entrega de paletes
            List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasPrateleiras =
                    resultadosMovimentoRobos.getTuplosPaletesArmazenadasPrateleiras();
            stockFacade.assinalaPaletesArmazenadas(tuplosPaletesArmazenadasPrateleiras);

            // processamento dos robos que entregaram as paletes, calculando a rota de regresso
            // ao estacionamento.
            processaRobosQueEntregaram(resultadosMovimentoRobos.getInfoRobosQueArmazenaram());


            // quando um robo termina o trajeto deve dar signal no lock e deve alterar o seu idDestino
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored){}
    }

    /**
     * Função responsável por aplicar as alterações necessárias no sistema relativas às paletes que foram recolhidas na
     * atual iteração da movimentação dos robôs. Esta irá marcar as paletes como recolhidas e calcular os trajetos dos
     * robos por elas responsáveis desde a área de recolha até à prateleira
     * @param mapPaleteInfoRoboInfo Map em que a chave é um tuplo composto pelo id de uma palete e o id da prateleira
     *                             a que se destina, sendo o valor um par composto pelo id do robo que a transporta e
     *                              pelas coordenadas atuais desse robo
     */
    private void processaRecolhaPaletes(Map<Tuple<Integer,Integer>,
            Tuple<Integer, Coordenadas>> mapPaleteInfoRoboInfo) {
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
    }

    /**
     * Função responsável por aplicar as alterações necessárias no sistema relativas aos robots que acabaram de
     * colocar a palete na prateleira e devem agora regressar ao seu local pré-definido
     * @param infoRobosQueArmazenaram map em que a chave será o id de um robô e o valor um tuplo que contém o Id da
     *                                zona pré-definida do robô assim como as suas coordenadas atuais.
     */
    private void processaRobosQueEntregaram(Map<Integer, Tuple<Integer, Coordenadas>> infoRobosQueArmazenaram){
        for(Map.Entry<Integer, Tuple<Integer,Coordenadas>> infoRoboQueArmazenou : infoRobosQueArmazenaram.entrySet()){
            int idRobo = infoRoboQueArmazenou.getKey();
            int idEstacionamento = infoRoboQueArmazenou.getValue().getO();
            Coordenadas coordenadasRobo = infoRoboQueArmazenou.getValue().getT();

            List<Coordenadas> trajetoAteEstacionamento = this.mapa.calculaTrajeto(idEstacionamento, coordenadasRobo);
            roboFacade.transmiteInfoRota(0,0,idRobo,trajetoAteEstacionamento,EstadoRobo.RETORNO);
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
