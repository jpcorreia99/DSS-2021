package Database;

import Business.Armazem.Robo.Robo;

import java.sql.*;
import java.util.*;

public class RoboDAO implements Map<Integer, Robo> {
    private static RoboDAO singleton = null;

    /**
     * Implementação do padrão Singleton
     *
     * @return devolve a instância única desta classe
     */
    public static RoboDAO getInstance() {
        if (RoboDAO.singleton == null) {
            RoboDAO.singleton = new RoboDAO();
        }
        return RoboDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;

        try (Connection conn = DBConnect.connect();
             Statement sta = conn.createStatement()) {
            String sql = "SELECT count(*) AS Total from Robo";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
                i = rs.getInt("Total");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    @Override
    public Robo get(Object key) {
        Robo r = null;

        try (Connection conn = DBConnect.connect();
             Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Robo WHERE id='"+key+"'");
            if (rs.next()) {  // A chave existe na tabela
                r = new Robo(rs.getInt("id"),
                        rs.getInt("x"), rs.getInt("y"),rs.getInt("idPrateleira"), rs.getInt("idPalete"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return r;
    }

    @Override
    public Robo put(Integer idRobo, Robo robo) {
        Robo res;

        try (Connection conn = DBConnect.connect();
             Statement stm = conn.createStatement()) {
            // Actualizar o Robo
            res = get(idRobo);
            stm.executeUpdate(
                    "INSERT INTO Robo VALUES (" + idRobo.toString() + ", " + robo.getCoordenadas().getX() + "," +
                            robo.getCoordenadas().getY()+","+
                            robo.getIdPrateleira() + "," + robo.getIdPalete() + ") "+
                             "ON DUPLICATE KEY UPDATE x=VALUES(x)," +
                            " y=VALUES(y),"+ " idPrateleira=VALUES(idPrateleira), idPalete=VALUES(idPalete)");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Collection<Robo> values() {
        List<Robo> col = new ArrayList<>();

        try (Connection conn = DBConnect.connect();
             Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT id FROM Robo");
            while (rs.next()) {   // Utilizamos o id para chamar o get e obter o robo
                col.add(this.get(rs.getString("id")));
            }
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return col;
    }



    @Override
    public boolean isEmpty() {
        throw new NullPointerException("method not implemented!");
    }

    @Override
    public boolean containsKey(Object o) {
        throw new NullPointerException("methodnot implemented!");
    }

    @Override
    public boolean containsValue(Object o) {
        throw new NullPointerException("method not implemented!");
    }

    @Override
    public Robo remove(Object o) {
        throw new NullPointerException("method not implemented!");
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Robo> map) {

    }

    @Override
    public void clear() {
        throw new NullPointerException("method not implemented!");
    }

    @Override
    public Set<Integer> keySet() {
        throw new NullPointerException("method not implemented!");
    }


    @Override
    public Set<Entry<Integer, Robo>> entrySet() {
        throw new NullPointerException("method not implemented!");
    }

}
