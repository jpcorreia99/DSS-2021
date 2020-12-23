package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {
    private static final String URL = "localhost";
    private static String DATABASE = "ArmazemInteligente";
    private static final String USERNAME = "dss";
    private static final String PASSWORD = "1234";
    private static Lock lock = new ReentrantLock();
    private static boolean initialized=false;

    private static int INITIAL_POOL_SIZE = 10; // numero maximo de conexões utilizadas
    private static List<Connection> connectionPool = new ArrayList<>(INITIAL_POOL_SIZE);;
    private static List<Connection> usedConnections = new ArrayList<>();

    public static void initialize() throws SQLException {
        if(!initialized) {
            initialized=true;
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + URL + "/" + DATABASE, USERNAME, PASSWORD);
                connectionPool.add(conn);
            }
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            lock.lock();
            conn = connectionPool
                    .remove(connectionPool.size() - 1);

            if (!conn.isValid(5)) {
                conn = DriverManager.getConnection("jdbc:mysql://" + URL +
                        "/" + DATABASE, USERNAME, PASSWORD);
            }
            usedConnections.add(conn);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            lock.unlock();
        }

        return conn;
    }

    public static void releaseConnection(Connection connection) {
        try {
            lock.lock();
            connectionPool.add(connection);
            usedConnections.remove(connection);
        } finally {
            lock.unlock();
        }
    }

    public static void fecharTodasLigacoes() {
        for(Connection conn: connectionPool){
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
