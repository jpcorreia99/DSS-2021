package Database;

import Util.EstadoPrateleira;
import Util.Tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static javax.swing.UIManager.getInt;

public class PrateleiraDAO {
    private static PrateleiraDAO singleton = null;

    /**inse
     * Devolve uma instância do objeto
     * @return Instância
     */
    public static PrateleiraDAO getInstance() {
        if(singleton == null)
            singleton = new PrateleiraDAO();
        return PrateleiraDAO.singleton;
    }

    public boolean armazemTemEspacoDisponivel() {
        Connection conn = ConnectionPool.getConnection();

        try (Statement sta = conn.createStatement()) {
            String sql = "SELECT * from Prateleira where estado="+ EstadoPrateleira.LIVRE.getValor()+";"; // ver se não tenho de marcar com mais nada para indicar que robô está a voltar
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

    public void inserePaletes(List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasZonas){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            for(Tuple<Integer,Integer> tuploPaletePrateleira: tuplosPaletesArmazenadasZonas) {
                stm.executeUpdate("UPDATE Prateleira"+
                        " SET estado="+ EstadoPrateleira.OCUPADA.getValor()+
                        ",idPalete ="+tuploPaletePrateleira.getO()+
                        " WHERE id ="+tuploPaletePrateleira.getT()+";");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public int encontraPrateleiraLivre(){
        int idPrateleira = 0;
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Prateleira WHERE estado='"+ EstadoPrateleira.LIVRE.getValor()+"'");
            if (rs.next()) {  // Existe alguma prateleira livre
                idPrateleira = rs.getInt("id");
                //assinalar que agora a prateleira já não estará livre (mas ainda não tem palete, ou seja, está comprometida a receber uma"
                String query ="UPDATE Prateleira"+
                        " SET estado="+ EstadoPrateleira.COMPROMETIDA.getValor()+
                        " WHERE id ="+idPrateleira+";";
                stm.executeUpdate(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return idPrateleira;
    }

    public int getIdPrateleiraQueGuardaPalete(int idPalete){
        Connection conn = ConnectionPool.getConnection();
        int idPrateleira = 0;

        try (Statement stm = conn.createStatement()) {
            String sql = "SELECT * from Prateleira where idPalete="+idPalete+";";
            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                idPrateleira = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return idPrateleira;
    }

    /**
     * Devolve lista de ids de prateleira ocupadas
     * @return
     */
    public List<Integer> getPrateleirasOcupadas(){
        Connection conn = ConnectionPool.getConnection();
        List<Integer> ids = new ArrayList<>();

        try (Statement stm = conn.createStatement()) {
            String sql = "SELECT * from Prateleira where estado="+EstadoPrateleira.OCUPADA.getValor()+";";
            ResultSet rs = stm.executeQuery(sql);
            while(rs.next()) {
                ids.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return ids;
    }
}
