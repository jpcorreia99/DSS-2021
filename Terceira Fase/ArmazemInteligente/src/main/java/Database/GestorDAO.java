package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import Business.Armazem.Gestor.Gestor;

public class GestorDAO {
    private static GestorDAO singleton = null;

    public static GestorDAO getInstance() {
        if (GestorDAO.singleton == null) {
            GestorDAO.singleton = new GestorDAO();
        }
        return GestorDAO.singleton;
    }


    public boolean userExiste(String username) throws NullPointerException {
        Connection conn = ConnectionPool.getConnection();

        try(Statement stm = conn.createStatement()) {
            String sql = "SELECT * FROM Gestor WHERE username='" + username +
                    "'";
            ResultSet rs = stm.executeQuery(sql);
            return rs.next();
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public Gestor get(String key) {
        Connection conn = ConnectionPool.getConnection();
        Gestor gestor = null;

        try(Statement stm = conn.createStatement()) {
            String sql = "SELECT * FROM Gestor WHERE username='" + key + "'";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next())
                    gestor = new Gestor(rs.getString(1), rs.getString(2));
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(conn);
        }

        return gestor;
    }
}
