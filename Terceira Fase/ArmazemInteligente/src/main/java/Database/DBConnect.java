package Database;

import Model.Armazem.Gestor.Gestor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect  {
    private static final String URL = "localhost";
    private static String DATABASE = "ArmazemInteligente";
    private static final String USERNAME = "dss";
    private static final String PASSWORD = "1234";

    public static void setupBD() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://"+URL+"/", USERNAME, PASSWORD);
        Statement s = conn.createStatement();

        s.executeUpdate("CREATE DATABASE IF NOT EXISTS ArmazemInteligente;");
        s.execute("USE ArmazemInteligente;");

        String[] gestores = {"tobias", "anacleto", "zeca"};
        String[] passwords = {"420noscope", "1234", "1234"};

        s.executeUpdate("CREATE DATABASE IF NOT EXISTS ArmazemInteligente;");
        s.execute("USE ArmazemInteligente;");
        s.execute("CREATE TABLE IF NOT EXISTS Gestor (username VARCHAR(45), password VARCHAR(45), PRIMARY KEY (username));");

        for (int i = 0; i < gestores.length; i++) {
            s.execute ("INSERT IGNORE INTO Gestor (username, password) VALUES ('" + gestores[i] + "', '" +
                    Gestor.generate(passwords[i]) + "');");
        }
    }

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://"+URL+"/" + DATABASE, USERNAME, PASSWORD);
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
