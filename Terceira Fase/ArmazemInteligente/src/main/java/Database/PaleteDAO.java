package Database;

import Business.Armazem.Stock.Palete;
import Util.EstadoPalete;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

    public boolean existemPaletesRecemChegadas(){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            String sql = "SELECT * from Palete where estado="+EstadoPalete.RECEM_CHEGADA.getValor()+";";
            ResultSet rs = stm.executeQuery(sql);
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

    public int getPaleteRecemChegada(){
        Connection conn = ConnectionPool.getConnection();
        int idPalete = 0;

        try (Statement stm = conn.createStatement()) {
            String sql = "SELECT * from Palete where estado="+EstadoPalete.RECEM_CHEGADA.getValor()+";";
            ResultSet rs = stm.executeQuery(sql);
            rs.next();
            idPalete = rs.getInt("id");
            String material = rs.getString("material");
            System.out.println("Foi selecionada a palete " + idPalete + ", com o material: " + material);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
        return idPalete;
    }

    public void marcaPaleteEmLevantamento(int idPalete){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            String sql = "UPDATE Palete" +
                    " SET estado="+ EstadoPalete.EM_LEVANTAMENTO.getValor()+
                    " WHERE id ="+idPalete+";";
            stm.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
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

     public void assinalaPaleteEmTransporte(int idPalete){
         Connection conn = ConnectionPool.getConnection();

         try (Statement stm = conn.createStatement()) {
             stm.executeUpdate("UPDATE Palete"+
                     " SET estado="+ EstadoPalete.TRANSPORTE.getValor()+
                     " WHERE id ="+idPalete+";");
         } catch (SQLException e) {
             e.printStackTrace();
             throw new NullPointerException(e.getMessage());
         }finally {
             ConnectionPool.releaseConnection(conn);
         }
    }

    public void assinalaPaletesArmazenadas(List<Integer> idsPaletesArmazenadas){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            for(Integer idPalete: idsPaletesArmazenadas) {
                stm.executeUpdate("UPDATE Palete"+
                        " SET estado="+ EstadoPalete.ARMAZENADA.getValor()+
                        " WHERE id ="+idPalete+";");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public List<Palete> getTodasPaletes() {
        Connection conn = ConnectionPool.getConnection();
        List<Palete> paletes = new ArrayList<>();

        try (Statement stm = conn.createStatement()) {
            String sql = "SELECT * from Palete;";
            ResultSet rs = stm.executeQuery(sql);
            while(rs.next()) {
                 paletes.add(new Palete(rs.getInt("id"), rs.getString("material"),
                         EstadoPalete.getEnumByValor(rs.getInt("estado"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return paletes;
    }

}
