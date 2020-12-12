package Business.Armazem.Gestor;

import Database.GestorDAO;

public class GestorFacade {
    GestorDAO gestorDAO;
    
    public GestorFacade () {
        gestorDAO = GestorDAO.getInstance();
    }

    public boolean login (String user, String password) {
        boolean res = false;

        if (gestorDAO.userExiste(user)) {
            Gestor g = gestorDAO.get(user);
            res = g.passwordCorreta(password);
        }
      
        return res;
    }
}
