package Business.Armazem.Stock;

import Business.Armazem.IStock;
import Database.PaleteDAO;
import Database.PrateleiraDAO;
import Util.EstadoPalete;
import Util.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StockFacade implements IStock {
    PaleteDAO paleteDAO;
    PrateleiraDAO prateleiraDAO;

    public StockFacade(){
        this.paleteDAO = PaleteDAO.getInstance();
        Palete.atualizaNumeroPaletes(paleteDAO.numeroPaletes());
        this.prateleiraDAO=PrateleiraDAO.getInstance();
    }

    public boolean existemPaletesRecemChegadas(){
        return this.paleteDAO.existemPaletesRecemChegadas();
    }

    public int getPaleteRecemChegada(){
        return this.paleteDAO.getPaleteRecemChegada();
    }

    public void marcaPaleteEmLevantamento(int idPalete){
        this.paleteDAO.marcaPaleteEmLevantamento(idPalete);
    }

    public void assinalaPaleteEmTransporte(int idPalete){
        this.paleteDAO.assinalaPaleteEmTransporte(idPalete);
    }

    public void assinalaPaletesArmazenadas(List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasPrateleiras){

        this.prateleiraDAO.inserePaletes(tuplosPaletesArmazenadasPrateleiras);

        // asinala nas próprias paletes que estão armazenadas
        List<Integer> idsPaletes = tuplosPaletesArmazenadasPrateleiras.stream().map(Tuple::getO).collect(Collectors.toList());
        this.paleteDAO.assinalaPaletesArmazenadas(idsPaletes);
    }

    public int encontraPrateleiraLivre(){
        return this.prateleiraDAO.encontraPrateleiraLivre();
    }

    public Map<Integer,Tuple<String, EstadoPalete>> getLocPaletes(){
        List<Palete> listaPaletes = paleteDAO.getTodasPaletes();
        Map<Integer, Tuple<String,EstadoPalete>> locPaletes = new HashMap<>();

        for(Palete palete : listaPaletes) {
            String material = palete.getMaterial();
            EstadoPalete estado = palete.getEstado();
            Tuple<String,EstadoPalete> tuploMaterialEstado = new Tuple<>(material,estado);

            locPaletes.put(palete.getId(), tuploMaterialEstado);
        }

        return locPaletes;
    }

    public int getIdPrateleiraGuardaPalete(int idPalete) {
        return prateleiraDAO.getIdPrateleiraQueGuardaPalete(idPalete);
    }

    /**
     * Devolve lista de ids de prateleira ocupadas
     * @return
     */
    public List<Integer> getPrateleirasOcupadas(){
        return this.prateleiraDAO.getPrateleirasOcupadas();
    }

}
