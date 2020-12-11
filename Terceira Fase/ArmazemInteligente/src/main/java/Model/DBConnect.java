package Model;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect  {
    private static final String URL = "localhost";
    private static String DATABASE = "ArmazemInteligente";
    private static final String USERNAME = "dss";
    private static final String PASSWORD = "1234";
    private static boolean isStartup = true;
    
    public static Connection connect() {
        Connection conn;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = isStartup ? 
            DriverManager.getConnection("jdbc:mysql://localhost/", USERNAME, PASSWORD) : 
            DriverManager.getConnection("jdbc:mysql://localhost/" + DATABASE, USERNAME, PASSWORD);
            
            isStartup = false;
            
            return conn;
            /*
            return DriverManager.getConnection("jdbc:mysql://" + URL
                    + "/" + DATABASE
                    + "?user=" + USERNAME
                    + "&password=" + PASSWORD
                    + "&allowMultiQueries=true");*/
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
