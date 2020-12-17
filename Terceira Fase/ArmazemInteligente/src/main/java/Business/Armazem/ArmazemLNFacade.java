package Business.Armazem;

import Business.Armazem.Gestor.GestorFacade;
import Business.Armazem.Robo.EstadoRobo;
import Business.Armazem.Robo.RoboFacade;
import Business.Armazem.Stock.StockFacade;
import Business.IArmazemLN;

import Requests.LeitorCodigosQR;
import Util.Coordenadas;
import Util.ResultadosMovimentoRobos;
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
    
    public Map<Integer, Tuple<String, Integer>> getPaletes() {
        Map<Integer, Tuple<String, Integer>> paletes = new HashMap<>();
         
        String[] materiais = {"Frangos", "Vibradores", "Tremoços", "Iogurtes", "Memes", "2ª fase de DSS", "Valérios", "Pinguins", "Esperanças", "Pantufas", "Caloiros"};
        Integer[] ys = {0, 7, 6, 4, 1, 8, 3, 2, 8, 2, 9};
        Integer[] xs = {7, 3, 3, 2, 9, 0, 0, 2, 3, 1, 11};
        Integer[] estado = {1, 2, 2, 3, 1, 4, 1, 3, 2, 4, 1};
        
        for (int i = 0; i < 10; i++)
            paletes.put(i+1, new Tuple<>(materiais[i], estado[i]));
         //return stockFacade.getLocPaletes();
         return paletes;
    }

    public boolean login (String user, String password) {
        return this.gestorFacade.login(user,password);
    }

    private void gereRobos(){
        while(funciona){
            long start2 = System.currentTimeMillis();
            escalonaRobos();
            moveRobos();

            // quando um robo termina o trajeto deve dar signal no lock e deve alterar o seu idDestino
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored){}

            long finish2 = System.currentTimeMillis();
            long timeElapsed2 = finish2 - start2;
//            System.out.println("Tempo gasto: "+timeElapsed2);
        }
    }

    private void escalonaRobos(){
        if(stockFacade.existemPaletesRecemChegadas() && roboFacade.existemRobosDisponiveis()){
            int idPalete =stockFacade.getPaleteRecemChegada();
            int idPrateleira = stockFacade.encontraPrateleiraLivre();
            if (idPrateleira != 0) {
                stockFacade.marcaPaleteEmLevantamento(idPalete);
                Tuple<Integer, Coordenadas> tuploIdCoordenadas =
                        roboFacade.encontraRoboLivre(idPalete); // falta implementar, deve marcar o robo como tendo uma palte
                List<Coordenadas> percursoInicial = new ArrayList<>();
                percursoInicial.add(new Coordenadas(tuploIdCoordenadas.getT().getX() - 1, tuploIdCoordenadas.getT().getY()));
                System.out.println("Escalonou-se o robo="+tuploIdCoordenadas.getO()+", idPalete=" + idPalete + ",idPrateleira" + idPrateleira);

                roboFacade.transmiteInfoRota(idPalete, idPrateleira, tuploIdCoordenadas.getO(), percursoInicial, EstadoRobo.RECOLHA);
            }else {
                System.out.println("Armazém cheio");
            }
        }else{
//            if(!stockFacade.existemPaletesRecemChegadas()) {
//                System.out.println("Não há paletes");
//            }else{
//                System.out.println("Não há robos");
//            }
        }
    }

    /**
     * Função que moverá processará no sistema todas as alterações resultantes da movimentação de todos os robos
     * com rotas por 1 time step.
     */
    private void moveRobos(){
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
            List<Coordenadas> rotaAtePrateleira = this.mapa.calculaRota(idPrateleira, coordenadasRobo);
//            System.out.println("Transmitida rota até à pratleira, robo: "+idRobo);
//            for(Coordenadas c : rotaAtePrateleira){
//                System.out.println(c.toString());
//            }
            roboFacade.transmiteInfoRota(idPalete, idPrateleira,idRobo, rotaAtePrateleira, EstadoRobo.TRANSPORTE);
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

            List<Coordenadas> rotaAteEstacionamento = this.mapa.calculaRota(idEstacionamento, coordenadasRobo);
//            System.out.println("Transmitida rota até à base, robo: "+idRobo);
//            for(Coordenadas c : rotaAteEstacionamento){
//                System.out.println(c.toString());
//            }
            roboFacade.transmiteInfoRota(0,0,idRobo,rotaAteEstacionamento,EstadoRobo.RETORNO);
        }
    }
    
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

    public void desligaSistema(){
        this.leitorCodigosQR.desliga();
        this.funciona=false;
    }
}
