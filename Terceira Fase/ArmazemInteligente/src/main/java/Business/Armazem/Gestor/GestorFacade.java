package Business.Armazem.Gestor;

import Database.GestorDAO;

public class GestorFacade {
    GestorDAO gestorDAO;
    Gestor userAtual;
    
    public GestorFacade () {
        gestorDAO = GestorDAO.getInstance();
    }

    public boolean login (String user, String password) {
        boolean res = false;

        if (gestorDAO.userExiste(user)) {
            Gestor g = gestorDAO.get(user);
            res = g.passwordCorreta(password);
        }
        
        if (res) {
            this.userAtual = new Gestor (user, password);
        }
        
        return res;
    }
}
