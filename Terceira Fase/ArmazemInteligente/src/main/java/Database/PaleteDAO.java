package Database;

import Business.Armazem.Stock.Palete;
import Util.Estado;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public Palete get(String paleteId) {
        Palete palete = null;

        try(Connection conn = DBConnect.connect();
        Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Robo WHERE id='"+paleteId+"'");
            if (rs.next()) {  // A chave existe na tabela
                Estado estado = Estado.getEnumByValor(rs.getInt("estado"));
                palete = new Palete(rs.getInt("id"),
                        rs.getString("material"),estado);
            }
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }

        return palete;
    }

    public void atualiza(Integer paleteId, Palete palete) {
        try (Connection conn = DBConnect.connect();
             Statement stm = conn.createStatement()) {
            stm.executeUpdate(
                    "INSERT INTO Palete VALUES (" + paleteId.toString() + ", " + palete.getMaterial() + "," +
                            palete.getEstado().getValor() + ") " +
                            "ON DUPLICATE KEY UPDATE material=VALUES(material)," +
                            " estado=VALUES(estado)");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public Palete getPaleteAEspera() {
        Palete palete = null;

        try (Connection conn = DBConnect.connect();
             Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Palete WHERE estado='"+Estado.ESPERA.getValor()+"'");
            if (rs.next()) {  // A chave existe na tabela
                palete = new Palete(rs.getInt("id"),
                        rs.getString("material"),Estado.ESPERA);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return palete;
    }
}
