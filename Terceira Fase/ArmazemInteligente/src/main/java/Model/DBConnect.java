package Model;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect  {
    private static final String URL = "localhost";
    private static final String DATABASE = "ArmazemInteligente";
    private static final String USERNAME = "pedro";
    private static final String PASSWORD = "1234";

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + URL
                    + "/" + DATABASE
                    + "?user=" + USERNAME
                    + "&password=" + PASSWORD
                    + "&allowMultiQueries=true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
