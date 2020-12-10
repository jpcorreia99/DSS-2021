package Model.Armazem.Gestor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Gestor {
    String username;
    String password;
    
    public Gestor (String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public void setPassword (String password) {
        this.password = password;
    }
    
    public static String generate(final String password) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest((password).getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean passwordCorreta (String password) {
        return this.password.equals(Gestor.generate(password));
    }
}

