package Database;

import Util.EstadoRobo;
import Business.Armazem.Robo.Robo;
import Util.Coordenadas;
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

    public boolean existemRobosDisponiveis(){
        Connection conn = ConnectionPool.getConnection();

        try (Statement sta = conn.createStatement()) {
            String sql = "SELECT * from Robo where estado="+EstadoRobo.LIVRE.getValor()+";"; // ver se não tenho de marcar com mais nada para indicar que robô está a voltar
            ResultSet rs = sta.executeQuery(sql);
            if(rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return false;
    }

    public Robo get(int idRobo) {
        Robo r = null;
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Robo WHERE id='" + idRobo + "'");
            if (rs.next()) {  // A chave existe na tabela
                r = new Robo(rs.getInt("id"),
                        rs.getInt("x"), rs.getInt("y"), rs.getInt("idEstacionamento"),
                        rs.getInt("idPrateleira"), rs.getInt("idPalete"), EstadoRobo.getEnumByValor(rs.getInt("estado")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        } finally {
            ConnectionPool.releaseConnection(conn);
        }

        return r;
    }

    public void atualiza(Robo robo) {
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            // Actualizar o Robo
            String sql = "UPDATE Robo SET x = "  + robo.getCoordenadas().getX() + "," +
                            " y = "+robo.getCoordenadas().getY()+","+
                            " idEstacionamento ="+robo.getZonaEstacionamento() +","+
                            " idPrateleira = "+robo.getIdPrateleira() + "," +
                            " idPalete = "+ robo.getIdPalete() + "," +
                            " estado = "+ robo.getEstado().getValor() +
                            " WHERE id = "+ robo.getId()+";";

            stm.executeUpdate(sql);

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

    public List<Robo> getRobos(){
        List<Robo> res = new ArrayList<>();
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
                ResultSet rs = stm.executeQuery("SELECT * FROM Robo");
            while (rs.next()) {  // A chave existe na tabela
                res.add( new Robo(rs.getInt("id"),
                        rs.getInt("x"), rs.getInt("y"),
                        rs.getInt("idEstacionamento"),
                        rs.getInt("idPrateleira"),
                        rs.getInt("idPalete"),
                        EstadoRobo.getEnumByValor(rs.getInt("estado"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return res;
    }


    /**
     * Devolve o robo que está a transportar a palete indicada
     * @param idPalete
     * @return
     */
    public Robo getRoboQueTransportaPalete(int idPalete){
        Robo res = null;
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
                ResultSet rs = stm.executeQuery("SELECT * FROM Robo WHERE idPalete='" + idPalete + "'");

            if (rs.next()) {  // A chave existe na tabela
                res = new Robo(rs.getInt("id"),
                        rs.getInt("x"), rs.getInt("y"),
                        rs.getInt("idEstacionamento"),
                        rs.getInt("idPrateleira"),
                        rs.getInt("idPalete"),
                        EstadoRobo.getEnumByValor(rs.getInt("estado")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return res;
    }
}
