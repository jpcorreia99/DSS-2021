package Database;

import Util.EstadoRobo;
import Transportation.RoboTransportador;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RoboTransportadorDAO {
    private static RoboTransportadorDAO singleton = null;

    /**
     * Implementação do padrão Singleton
     *
     * @return devolve a instância única desta classe
     */
    public static RoboTransportadorDAO getInstance() {
        if (RoboTransportadorDAO.singleton == null) {
            RoboTransportadorDAO.singleton = new RoboTransportadorDAO();
        }
        return RoboTransportadorDAO.singleton;
    }

    public RoboTransportador getRoboTransportador(int idRobo){
        RoboTransportador r = null;
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Robo WHERE id='" + idRobo + "'");
            if (rs.next()) {  // A chave existe na tabela
                r = new RoboTransportador(rs.getInt("id"),
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

    public void leDadosRoboTransportador(RoboTransportador roboTransportador){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            // Actualizar o Robo
            String sql = "Select * from Robo where id="+roboTransportador.getId();

            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                roboTransportador.setIdPalete(rs.getInt("idPalete"));
                roboTransportador.setIdPrateleira(rs.getInt("idPrateleira"));
                roboTransportador.setEstado(EstadoRobo.getEnumByValor(rs.getInt("estado")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public void atualizaDadosRoboTransportador(RoboTransportador roboTransportador){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            // Actualizar o Robo
            String sql = "UPDATE Robo SET x = "  + roboTransportador.getCoordenadas().getX() + "," +
                    " y = "+roboTransportador.getCoordenadas().getY()+","+
                    " idEstacionamento ="+roboTransportador.getZonaEstacionamento() +","+
                    " idPrateleira = "+roboTransportador.getIdPrateleira() + "," +
                    " idPalete = "+ roboTransportador.getIdPalete() + "," +
                    " estado = "+ roboTransportador.getEstado().getValor() +
                    " WHERE id = "+ roboTransportador.getId()+";";

            stm.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }
}
