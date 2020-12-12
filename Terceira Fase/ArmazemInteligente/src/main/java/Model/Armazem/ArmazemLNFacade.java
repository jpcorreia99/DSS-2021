package Model.Armazem;

import Model.Armazem.Gestor.GestorFacade;
import Model.Armazem.Robo.RoboFacade;
import Model.Armazem.Stock.Palete;
import Model.Armazem.Stock.StockFacade;
import Model.IArmazemLN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmazemLNFacade implements IArmazemLN {
    RoboFacade roboFacade;
    StockFacade stockFacade;
    GestorFacade gestorFacade; 
    Mapa mapa;
    
    public ArmazemLNFacade () {
        roboFacade = new RoboFacade();
        stockFacade = new StockFacade();
        gestorFacade = new GestorFacade();
        mapa = new Mapa();
    }
    
    public Map<Integer, Palete> getPaletes() {
         return new HashMap<>(); 
    }
    
    public boolean login (String user, String password) {
        return this.gestorFacade.login(user,password);
    }
    
    public Map <Integer, List<Integer>> getMapa () {
        return mapa.getMapa();
    }
}
