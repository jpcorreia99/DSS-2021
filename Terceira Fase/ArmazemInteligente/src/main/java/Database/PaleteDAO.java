package Database;

import Business.Armazem.Stock.Palete;
import Util.EstadoPalete;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PaleteDAO {
    private static PaleteDAO singleton = null;
    /**
     * Implementação do padrão Singleton
     *
     * @return devolve a instância única desta classe
     */
    public static PaleteDAO getInstance() {
        if (PaleteDAO.singleton == null) {
            PaleteDAO.singleton = new PaleteDAO();
        }
        return PaleteDAO.singleton;
    }

    public void addNovaPalete(String material){
        Palete palete = new Palete(material);
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            String s = "INSERT INTO Palete VALUES (" + palete.getId() + ",'" + palete.getMaterial() + "'," +
                    EstadoPalete.RECEM_CHEGADA.getValor() + ");";
            stm.execute(s);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public Integer getPaleteRecemChegada(){
        Integer idPalete = null;
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            String sql = "SELECT * from Palete where estado=1";
            ResultSet rs = stm.executeQuery(sql);
            if(rs.next())
                idPalete = rs.getInt("id");
                sql = "UPDATE Palete" +
                        " SET estado="+ EstadoPalete.ESPERA.getValor()+
                        " WHERE id ="+idPalete+";";
                stm.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return idPalete;
    }


    public int numeroPaletes() {
        int i = 0;
        Connection conn = ConnectionPool.getConnection();

        try (Statement sta = conn.createStatement()) {
            String sql = "SELECT count(*) AS Total from Palete";
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

    public void assinalaPaletesArmazenadas(List<Integer> idsPaletesArmazenadas){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            for(Integer idPalete: idsPaletesArmazenadas) {
                stm.executeUpdate("UPDATE Palete"+
                        " SET estado="+ EstadoPalete.ARMAZENADA.getValor()+
                        ", WHERE id ="+idPalete+";)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public void atualiza(Integer paleteId, Palete palete) {
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            stm.executeUpdate(
                    "INSERT INTO Palete VALUES (" + paleteId.toString() + ", " + palete.getMaterial() + "," +
                            palete.getEstado().getValor() + ") " +
                            "ON DUPLICATE KEY UPDATE material=VALUES(material)," +
                            " estado=VALUES(estado)");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public Palete getPaleteAEspera() {
        Connection conn = ConnectionPool.getConnection();
        Palete palete = null;

        try (Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Palete WHERE estado='"+ EstadoPalete.RECEM_CHEGADA.getValor()+"'");
            if (rs.next()) {  // A chave existe na tabela
                palete = new Palete(rs.getInt("id"),
                        rs.getString("material"), EstadoPalete.RECEM_CHEGADA);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return palete;
    }
}
