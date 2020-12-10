package Model.Armazem.Gestor;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Model.DBConnect;

public class GestorDAO {
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

    public Gestor get(String key) {
        Connection conn = DBConnect.connect();
        try {
            Gestor gestor = null;
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM Gestores WHERE user='" + key + "'";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next())
                    gestor = new Gestor(rs.getString(1), rs.getString(2));
            return gestor;
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            DBConnect.close(conn);
        }
    }
}
