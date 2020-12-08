import Model.Armazem.Utilizador.UtilizadorDAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class APP {
    public static void main(String[] args) {
        UtilizadorDAO utilizadorDAO = new UtilizadorDAO();
        System.out.println(utilizadorDAO.userExiste("teste"));
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


}
