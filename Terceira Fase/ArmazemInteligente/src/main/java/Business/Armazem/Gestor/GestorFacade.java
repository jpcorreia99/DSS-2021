package Business.Armazem.Gestor;

import Business.Armazem.IGestor;
import Database.GestorDAO;

public class GestorFacade implements IGestor {
    GestorDAO gestorDAO;
    Gestor userAtual;
    
    public GestorFacade () {
        gestorDAO = GestorDAO.getInstance();
    }

    public boolean login (String user, String password) {
        boolean res = false;

        if (gestorDAO.userExiste(user)) {
            Gestor g = gestorDAO.get(user);
            if ((res = g.passwordCorreta(password)))
                this.userAtual = g;
        }
        
        return res;
    }
}
