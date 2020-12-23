package Database;

import Util.Coordenadas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// TODO devolver coordenada (0,0) caso exista problema de leitura

public class RotaDAO {
    private static RotaDAO singleton = null;
    private final Lock lock;

    public RotaDAO(){
        this.lock = new ReentrantLock();
    }

    /**
     * Implementação do padrão Singleton
     *
     * @return devolve a instância única desta classe
     */
    public static RotaDAO getInstance() {
        if (RotaDAO.singleton == null) {
            RotaDAO.singleton = new RotaDAO();
        }
        return RotaDAO.singleton;
    }


    public void adicionaRota(int idRobo, List<Coordenadas> rota){
        Connection conn = ConnectionPool.getConnection();

        try(Statement stm = conn.createStatement()){
            lock.lock();

            StringBuilder sb = new StringBuilder();
            for (Coordenadas coordenadas : rota){
                sb.append(coordenadas.getX()).append(",").append(coordenadas.getY()).append("/");
            }
            String rotaCodificada = sb.toString();
            String sql = "INSERT INTO Rota VALUES (" + idRobo + ", '" + rotaCodificada +"') "+
                        "ON DUPLICATE KEY UPDATE valor=VALUES(valor)";
            stm.execute(sql);
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
            lock.unlock();
        }
    }


    public Coordenadas getProximoPasso(int idRobo){
        Connection conn = ConnectionPool.getConnection();
        Coordenadas proximoPasso = null;

        try (Statement stm = conn.createStatement()) {
            lock.lock();
            String sql = "SELECT * from Rota where idRobo="+idRobo+";";
            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                String rotaCodificada = rs.getString("valor");
                List<Coordenadas> rotaDescodificada = descodificaRota(rotaCodificada);

                if(!rotaDescodificada.isEmpty()) {
                    proximoPasso = rotaDescodificada.remove(0);
                } else{
                    proximoPasso = new Coordenadas(0,0);
                }

                // se a lista estiver vazia depois de remover o próximo passo, remove-se a entrada na tabela
                if (rotaDescodificada.isEmpty()) {
                    String queryDeEliminacao = "DELETE from Rota where idRobo=" + idRobo + ";";
                    stm.executeUpdate(queryDeEliminacao);
                } else { // atualiza a rota
                    adicionaRota(idRobo, rotaDescodificada);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
            lock.unlock();
        }

        return proximoPasso;
    }

    public boolean rotaTerminou(int idRobo){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            lock.lock();
            String sql = "SELECT * from Rota where idRobo="+idRobo+";";
            ResultSet rs = stm.executeQuery(sql);
            return !rs.next(); // se tiver alguma entrada significa que ainda existe a rota,
                              // visto que quando ela termian é eliminada da bd
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
            lock.unlock();
        }

        return true;
    }

    private List<Coordenadas> descodificaRota(String coordenadasCodificadas){
        List<Coordenadas> res = new ArrayList<>();

        if(!coordenadasCodificadas.isBlank() || coordenadasCodificadas.equals("/")) {
            String[] coordenadasTexto = coordenadasCodificadas.split("/");
            for (String coordenadaTexto : coordenadasTexto) {
                String[] coordenada_x_y = coordenadaTexto.split(",");
                int x = Integer.parseInt(coordenada_x_y[0]);
                int y = Integer.parseInt(coordenada_x_y[1]);
                res.add(new Coordenadas(x, y));
            }
        }
        return res;
    }
}
