package Business.Armazem.Stock;

import Business.Armazem.IStock;
import Database.PaleteDAO;
import Database.PrateleiraDAO;
import Util.Tuple;

import java.util.List;
import java.util.stream.Collectors;

public class StockFacade implements IStock {
    PaleteDAO paleteDAO;
    PrateleiraDAO prateleiraDAO;

    public StockFacade(){
        this.paleteDAO = PaleteDAO.getInstance();
        Palete.atualizaNumeroPaletes(paleteDAO.numeroPaletes());
        this.prateleiraDAO=PrateleiraDAO.getInstance();
    }

    public void assinalaPaletesArmazenadas(List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasPrateleiras){

        this.prateleiraDAO.adicionaPaletes(tuplosPaletesArmazenadasPrateleiras);

        // asinala nas próprias paletes que estão armazenadas
        List<Integer> idsPaletes = tuplosPaletesArmazenadasPrateleiras.stream().map(Tuple::getO).collect(Collectors.toList());
        this.paleteDAO.assinalaPaletesArmazenadas(idsPaletes);
    }

    public int encontraPrateleiraLivre(){
        return this.prateleiraDAO.encontraPrateleiraLivre();
    }
//    public int encontraPrateleiraLivre() {
////        prateleiraDAO.encontraPrateleiraLivre();
//    }
}
