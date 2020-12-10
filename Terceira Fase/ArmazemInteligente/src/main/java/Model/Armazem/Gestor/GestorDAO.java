package Model.Armazem.Utilizador;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Model.DBConnect;

public class UtilizadorDAO {


    public boolean userExiste(String username) throws NullPointerException {
        Connection conn = DBConnect.connect();
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM Utilizador WHERE username='" + username +
                    "'";
            ResultSet rs = stm.executeQuery(sql);
            return rs.next();
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            DBConnect.close(conn);
        }
    }

//    public Utilizador get(Object key) {
//        Connection conn = DBConnect.connect();
//        try {
//            Utilizador al = null;
//            Statement stm = conn.createStatement();
//            String sql = "SELECT * FROM Utilizadores WHERE email='" + key + "'";
//            ResultSet rs = stm.executeQuery(sql);
//            if (rs.next())
//                if (rs.getBoolean(4))
//                    al = new Administrador(rs.getString(1), rs.getString(2),
//                            rs.getString(3));
//                else
//                    al = new Utilizador(rs.getString(1), rs.getString(2),
//                            rs.getString(3));
//            return al;
//        } catch (Exception e) {
//            throw new NullPointerException(e.getMessage());
//        } finally {
//            DBConnect.close(conn);
//        }
//    }
}
