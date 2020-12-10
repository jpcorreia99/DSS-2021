package Model.Armazem.Gestor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GestorFacade {
    GestorDAO gestorDAO;
    
    public GestorFacade () {
        gestorDAO = new GestorDAO();
    }
    
    
    
    public boolean login (String user, String password) {
        boolean res;
        
        if ((res = gestorDAO.userExiste(user))) {
            Gestor g = gestorDAO.get(user);
            res = g.passwordCorreta(password);
        }
        
        return res;
    }
}
