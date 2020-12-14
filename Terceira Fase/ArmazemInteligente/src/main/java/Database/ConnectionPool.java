package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private static final String URL = "localhost";
    private static String DATABASE = "ArmazemInteligente";
    private static final String USERNAME = "dss";
    private static final String PASSWORD = "1234";

    private static int INITIAL_POOL_SIZE = 3;
    private static List<Connection> connectionPool = new ArrayList<>(INITIAL_POOL_SIZE);;
    private static List<Connection> usedConnections = new ArrayList<>();

    public static void initialize() throws SQLException {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            Connection conn = DriverManager.getConnection("jdbc:mysql://"+URL+"/" + DATABASE, USERNAME, PASSWORD);
            connectionPool.add(conn);
        }
    }

    public static Connection getConnection() {
        Connection connection = connectionPool
                .remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public static boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

}
