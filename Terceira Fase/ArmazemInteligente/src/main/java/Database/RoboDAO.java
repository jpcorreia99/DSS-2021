package Database;

import Business.Armazem.Robo.Robo;
import Util.Coordenadas;
import Business.Armazem.Robo.EstadoRobo;
import Util.Tuple;

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

    public boolean existeRoboDisponivel(){
        Connection conn = ConnectionPool.getConnection();

        try (Statement sta = conn.createStatement()) {
            String sql = "SELECT count(*) AS Total from Robo where estado="+EstadoRobo.LIVRE.getValor()+";"; // ver se não tenho de marcar com mais nada para indicar que robô está a voltar
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next()) {
                return rs.getInt("Total")!=0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return false;
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
                        rs.getInt("idPrateleira"), rs.getInt("idPalete"), EstadoRobo.getEnumByValor(rs.getInt("estado")));
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
                            robo.getIdPrateleira() + "," + robo.getIdPalete() + "," +
                            robo.getEstado().getValor() +") "+
                             "ON DUPLICATE KEY UPDATE x=VALUES(x)," +
                            " y=VALUES(y),"+"idEstacionamento=Values(idEstacionamento)," +
                            " idPrateleira=VALUES(idPrateleira), idPalete=VALUES(idPalete), estado=VALUES(estado)");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    /**
     * devolve o Id de um robô que esteja disponível para transporte, atriuindo-lhe a palete que irá transportar
     * @param idPalete
     * @return
     */
    public Tuple<Integer, Coordenadas> encontraRoboLivre(int idPalete){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Robo WHERE estado='" + EstadoRobo.LIVRE.getValor() + "';");
            rs.next();
            int idRobo = rs.getInt("id");
            Coordenadas coodernadasRobo =  new Coordenadas(rs.getInt("x"), rs.getInt("y"));

            //atribui a palete ao robo
            stm.executeUpdate("UPDATE Robo"+
                             " SET idPalete="+idPalete+
                             " WHERE id ="+idRobo+";");

            return new Tuple<>(idRobo,coodernadasRobo);
        }catch (SQLException e) {
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
                            rs.getInt("idPalete"),
                            EstadoRobo.getEnumByValor(rs.getInt("estado"))));
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

                stm.executeUpdate("INSERT INTO Robo VALUES (" + roboId.toString() + ", " +
                        robo.getCoordenadas().getX() + "," +
                        robo.getCoordenadas().getY()+","+ robo.getZonaEstacionamento() +","+
                        robo.getIdPrateleira() + "," + robo.getIdPalete() + "," +
                        robo.getEstado().getValor() +") "+
                        "ON DUPLICATE KEY UPDATE x=VALUES(x)," +
                        " y=VALUES(y),"+"idEstacionamento=Values(idEstacionamento)," +
                        " idPrateleira=VALUES(idPrateleira), idPalete=VALUES(idPalete), estado=VALUES(estado)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }
}
