package Business.Armazem;

import Business.Armazem.Gestor.GestorFacade;
import Util.EstadoRobo;
import Business.Armazem.Robo.RoboFacade;
import Business.Armazem.Stock.StockFacade;
import Business.IArmazemLN;

import Util.Coordenadas;
import Util.ResultadosMovimentoRobos;
import Util.Tuple;
import Util.EstadoPalete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmazemLNFacade implements IArmazemLN {
    RoboFacade roboFacade;
    StockFacade stockFacade;
    GestorFacade gestorFacade;
    Boolean funcional;
    Mapa mapa;

    public ArmazemLNFacade () {
        roboFacade = new RoboFacade();
        stockFacade = new StockFacade();
        gestorFacade = new GestorFacade();
        mapa = new Mapa();
        funcional =true;
    }

    public void start() {
        Thread threadEscalonamentoRobos = new Thread(this::gereRobos);
        threadEscalonamentoRobos.start();
    }

    /**
     * Devolve um conjunto de informação relativa a cada palete existente no sistema
     * @return Map em que a chave é o id de uma palete e o valor é uma estrutura composta por:
     * tuplo composto pelo material da palete e um segundo tuplo
     * composto pelo estado da palete e as suas coordenadas
     */
    public Map<Integer,Tuple<String,Tuple<EstadoPalete,Coordenadas>>> getPaletes() {
        Map<Integer,Tuple<String,Tuple<EstadoPalete,Coordenadas>>> infoSobrePaletes = new HashMap<>();

        Map<Integer,Tuple<String, EstadoPalete>> mapIdMaterialEstado = stockFacade.getLocPaletes();
        for(Map.Entry<Integer,Tuple<String,EstadoPalete>> entry : mapIdMaterialEstado.entrySet()){
            int idPalete = entry.getKey();
            Tuple<String,EstadoPalete> tuploMaterialEstado = entry.getValue();
            String material = tuploMaterialEstado.getO();
            EstadoPalete estadoPalete = tuploMaterialEstado.getT();

            Coordenadas coordenadasPalete;
            if(estadoPalete==EstadoPalete.RECEM_CHEGADA || estadoPalete==EstadoPalete.EM_LEVANTAMENTO){
                coordenadasPalete = this.mapa.getCoordsZonaLevantamento();
            } else if(estadoPalete == EstadoPalete.TRANSPORTE) {
                coordenadasPalete = this.roboFacade.getCoordPalete(idPalete);
            } else{ // (estadoPalete== EstadoPalete.ARMAZENADA)
                int idPrateleira = stockFacade.getIdPrateleiraGuardaPalete(idPalete);
                if(idPrateleira!=0) {
                    coordenadasPalete = this.mapa.getCoords(idPrateleira);
                } else{
                    coordenadasPalete = new Coordenadas(0,0);
                }
            }
            Tuple<EstadoPalete,Coordenadas> tuploEstadoCoordenadas = new Tuple<>(estadoPalete,coordenadasPalete);
            Tuple<String,Tuple<EstadoPalete,Coordenadas>> tuploMaterialInfo = new Tuple<>(material,tuploEstadoCoordenadas);
            infoSobrePaletes.put(idPalete,tuploMaterialInfo);
        }
        return infoSobrePaletes;
    }

    public boolean login (String user, String password) {
        return this.gestorFacade.login(user,password);
    }

    /**
     * Função principal de gestão do sistema, responsável por escalonar os robos e paletes e tratar as notificações
     * por eles enviadas relativas ao transporte delas.
     */
    private void gereRobos(){
        while(funcional){
            escalonaRobos();
            atualizaSistema();

            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored){}
        }
    }

    /**
     * Função responsável por atribuir uma palete recem chegada (caso exista) um robo disponível
     */
    private void escalonaRobos(){
        if(stockFacade.armazemTemEspacoDisponivel()) {// Armazem não está cheio
            if (stockFacade.existemPaletesRecemChegadas() && roboFacade.existemRobosDisponiveis()) {
                int idPalete = stockFacade.getPaleteRecemChegada();
                int idPrateleira = stockFacade.encontraPrateleiraLivre();
                    stockFacade.marcaPaleteEmLevantamento(idPalete);
                    Tuple<Integer, Coordenadas> tuploIdCoordenadas =
                            roboFacade.encontraRoboLivre(idPalete); // falta implementar, deve marcar o robo como tendo uma palte

                    List<Coordenadas> percursoInicial = new ArrayList<>();
                    percursoInicial.add(new Coordenadas(tuploIdCoordenadas.getT().getX() - 1, tuploIdCoordenadas.getT().getY()));

                    roboFacade.transmiteInfoRota(idPalete, idPrateleira, tuploIdCoordenadas.getO(), percursoInicial, EstadoRobo.RECOLHA);
            }
        }
    }


    /**
     * Função responsável por aplicar as mudanças no sistema relativas às alterações causadas pelas notificações
     * recebidas no sistema enviadas pelos vários robos
     */
    private void atualizaSistema(){
            ResultadosMovimentoRobos resultadosMovimentoRobos = roboFacade.recolheNotificacoes();
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

            stockFacade.assinalaPaleteEmTransporte(idPalete);
            List<Coordenadas> rotaAtePrateleira;
            if(!(coordenadasRobo.getX()==0 || coordenadasRobo.getY() == 0)) {
                rotaAtePrateleira = this.mapa.calculaRota(idPrateleira, coordenadasRobo);
            }else { // ocorreu erro -> devolver robo à zona de estacionamento
                rotaAtePrateleira = new ArrayList<>();
                rotaAtePrateleira.add(this.mapa.getCoords(roboFacade.getIdZonaEstacionamento(idRobo)));
            }

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

            List<Coordenadas> rotaAteEstacionamento;
            if(!(coordenadasRobo.getX()==0 || coordenadasRobo.getY() == 0)) {
                rotaAteEstacionamento = this.mapa.calculaRota(idEstacionamento, coordenadasRobo);
            }else { // ocorreu erro -> devolver robo à zona de estacionamento
                rotaAteEstacionamento = new ArrayList<>();
                rotaAteEstacionamento.add(this.mapa.getCoords(roboFacade.getIdZonaEstacionamento(idRobo)));
            }
            roboFacade.transmiteInfoRota(0,0,idRobo,rotaAteEstacionamento,EstadoRobo.RETORNO);
        }
    }

    /**
     * Devolve uma versão do mapa preenchina com os robos e prateleiras ocupadas
     * @return
     */
    public int[][] getMapa () {
        int[][] map = mapa.getMapa();
        List<Integer> prateleiras = stockFacade.getPrateleirasOcupadas();
        List<Tuple<Coordenadas, Boolean>> robos = roboFacade.getInfoRobos();
        
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
        this.funcional =false;
    }
}
