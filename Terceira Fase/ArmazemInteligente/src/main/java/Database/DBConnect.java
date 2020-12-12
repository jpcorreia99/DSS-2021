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
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.getConnection("jdbc:mysql://"+URL+"/" + DATABASE, USERNAME, PASSWORD);
        }catch (SQLException e){
            inicializaBD();
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

    private static void inicializaBD() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://"+URL+"/", USERNAME, PASSWORD);
        Statement stm = conn.createStatement();

        stm.executeUpdate("CREATE DATABASE ArmazemInteligente;");
        stm.execute("USE ArmazemInteligente;");

        inicializaGestores(stm);
        inicializaRobos(stm);

    }

    private static void inicializaGestores(Statement stm) throws SQLException {
        String[] gestores = {"tobias", "anacleto", "zeca"};
        String[] passwords = {"420noscope", "1234", "1234"};

        stm.execute("CREATE TABLE Gestor (username VARCHAR(45), password VARCHAR(45), PRIMARY KEY (username));");

        for (int i = 0; i < gestores.length; i++) {
            stm.execute ("INSERT IGNORE INTO Gestor (username, password) VALUES ('" + gestores[i] + "', '" +
                    Gestor.generate(passwords[i]) + "');");
        }
    }

    private static void inicializaRobos(Statement stm) throws SQLException {

        String sql = "CREATE TABLE Robo (" +
                "  `id` INT NOT NULL,\n" +
                "  `x` INT NOT NULL,\n" +
                "  `y` INT NOT NULL,\n" +
                "  `idPrateleira` INT NOT NULL,\n" +
                "  `idPalete` INT NOT NULL,\n" +
                "  PRIMARY KEY (`id`))";

        stm.execute(sql);

        stm.execute("INSERT INTO Robo VALUES (1,0,0,0,0)");
        stm.execute("INSERT INTO Robo VALUES (2,1,0,0,0)");
        stm.execute("INSERT INTO Robo VALUES (3,0,1,0,0)");
    }
}
