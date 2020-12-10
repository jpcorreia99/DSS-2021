package Model.Armazem.Gestor;

public class GestorFacade {
    GestorDAO gestorDAO;
    
    public GestorFacade () {
        gestorDAO = new GestorDAO();
    }
    
    
    
    public boolean login (String user, String password) {
        boolean res = false;
        //System.out.println("O " + user + " dá " + gestorDAO.userExiste(user));
        if (gestorDAO.userExiste(user)) {
            Gestor g = gestorDAO.get(user);
            res = g.passwordCorreta(password);
        }
      
        return res;
    }
}
