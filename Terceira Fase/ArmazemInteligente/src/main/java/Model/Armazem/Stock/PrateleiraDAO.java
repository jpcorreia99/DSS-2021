package Model.Armazem.Stock;

import Model.DBConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PrateleiraDAO implements Map<Integer,Prateleira> {
    public PrateleiraDAO() {
        Connection conn = DBConnect.connect();
        try {
            Statement sta = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS Prateleira (\n" +
                    "  id INT NOT NULL PRIMARY KEY,\n" +
                    "  estaOcupado TINYINT NOT NULL);";
            sta.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnect.close(conn);
        }
    }

    @Override
    public int size() {
        int i = 0;
        Connection conn = DBConnect.connect();
        try {
            Statement sta = conn.createStatement();
            String sql = "SELECT count(*) AS Total from Prateleira";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
                i = rs.getInt("Total");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnect.close(conn);
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Prateleira get(Object key) {

        return null;
    }

    @Override
    public Prateleira put(Integer key, Prateleira value) {
        return null;
    }

    @Override
    public Prateleira remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Prateleira> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<Integer> keySet() {
        return null;
    }

    @Override
    public Collection<Prateleira> values() {
        return null;
    }

    @Override
    public Set<Entry<Integer, Prateleira>> entrySet() {
        return null;
    }
}
