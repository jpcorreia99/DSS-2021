package Model.Armazem.Stock;

import Model.DBConnect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PaleteDAO {
    private static PaleteDAO singleton = null;

    public PaleteDAO() {
        try (Connection conn = DBConnect.connect()) {
            Statement sta = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
