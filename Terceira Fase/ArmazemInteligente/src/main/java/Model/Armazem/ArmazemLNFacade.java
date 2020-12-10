package Model.Armazem;

import Model.Armazem.Gestor.GestorFacade;
import Model.Armazem.Robo.RoboFacade;
import Model.Armazem.Stock.Palete;
import Model.Armazem.Stock.StockFacade;
import Model.IArmazemLN;
import java.util.HashMap;
import java.util.Map;

public class ArmazemLNFacade implements IArmazemLN {
    RoboFacade roboFacade;
    StockFacade stockFacade;
    GestorFacade gestorFacade; 
    
    public ArmazemLNFacade () {
        roboFacade = new RoboFacade();
        stockFacade = new StockFacade();
        gestorFacade = new GestorFacade();
    }
    
    public Map<Integer, Palete> getPaletes() {
         return new HashMap<>(); 
    }
}
