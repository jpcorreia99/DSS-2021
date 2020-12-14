package Database;

import Business.Armazem.Robo.Robo;

import java.sql.*;
import java.util.*;

public class RoboDAO {
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

    public int size() {
        int i = 0;
        Connection conn = ConnectionPool.getConnection();

        try (Statement sta = conn.createStatement()) {
            String sql = "SELECT count(*) AS Total from Robo";
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next())
                i = rs.getInt("Total");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return i;
    }

    public Robo get(Object roboId) {
        Robo r = null;
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Robo WHERE id='"+roboId+"'");
            if (rs.next()) {  // A chave existe na tabela
                r = new Robo(rs.getInt("id"),
                        rs.getInt("x"), rs.getInt("y"),rs.getInt("idEstacionamento"),
                        rs.getInt("idPrateleira"), rs.getInt("idPalete"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return r;
    }

    public void put(Integer roboId, Robo robo) {
        Connection conn = DBConnect.connect();

        try (Statement stm = conn.createStatement()) {
            // Actualizar o Robo
            stm.executeUpdate(
                    "INSERT INTO Robo VALUES (" + roboId.toString() + ", " + robo.getCoordenadas().getX() + "," +
                            robo.getCoordenadas().getY()+","+ robo.getZonaEstacionamento() +","+
                            robo.getIdPrateleira() + "," + robo.getIdPalete() + ") "+
                             "ON DUPLICATE KEY UPDATE x=VALUES(x)," +
                            " y=VALUES(y),"+"idEstacionamento=Values(idEstacionamento)," +
                            " idPrateleira=VALUES(idPrateleira), idPalete=VALUES(idPalete)");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public Map<Integer,Robo> getRobos(Set<Integer> listaRoboId){
        Map<Integer,Robo> res = new HashMap<>();
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            for(Integer roboId: listaRoboId) {
                ResultSet rs = stm.executeQuery("SELECT * FROM Robo WHERE id='" + roboId + "'");
                if (rs.next()) {  // A chave existe na tabela
                    res.put(roboId, new Robo(rs.getInt("id"),
                            rs.getInt("x"), rs.getInt("y"),
                            rs.getInt("idEstacionamento"),
                            rs.getInt("idPrateleira"),
                            rs.getInt("idPalete")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return res;
    }

    public void atualizaRobos(Map<Integer,Robo> robosEmTransito){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            // Actualizar o Robo
            for(Map.Entry<Integer,Robo> entrada : robosEmTransito.entrySet()) {
                Robo robo = entrada.getValue();
                Integer roboId = entrada.getKey();
                stm.executeUpdate(
                        "INSERT INTO Robo VALUES (" + roboId.toString() + ", " + robo.getCoordenadas().getX() + "," +
                                robo.getCoordenadas().getY() + "," + robo.getZonaEstacionamento() + "," +
                                robo.getIdPrateleira() + "," + robo.getIdPalete() + ") " +
                                "ON DUPLICATE KEY UPDATE x=VALUES(x)," +
                                " y=VALUES(y)," + "idEstacionamento=Values(idEstacionamento)," +
                                " idPrateleira=VALUES(idPrateleira), idPalete=VALUES(idPalete)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }
}
