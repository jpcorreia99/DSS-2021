package Database;

import Util.DirecionalidadeNotificacao;
import Util.Notificacao;
import Util.TipoNotificacao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NotificacaoDAO {
    private static NotificacaoDAO singleton = null;

    private Semaphore mutex;
    /**
     * Implementação do padrão Singleton
     *
     * @return devolve a instância única desta classe
     */
    public static NotificacaoDAO getInstance() {
        if (NotificacaoDAO.singleton == null) {
            NotificacaoDAO.singleton = new NotificacaoDAO();
        }
        return NotificacaoDAO.singleton;
    }

    public NotificacaoDAO(){
        this.mutex = new Semaphore(1);;
    }

    public void enviarNotificacao(Notificacao notificacao, DirecionalidadeNotificacao direcionalidadeNotificacao){
        Connection conn = ConnectionPool.getConnection();

        try {
            mutex.acquire();

            Statement stm = conn.createStatement();
                String s = "INSERT INTO Notificacao (idRobo,tipo,direcionalidade) VALUES (" +
                        notificacao.getIdRobo() + ", " +
                        notificacao.getTipo().getValor() + ", " +
                        direcionalidadeNotificacao.getValor() + ")";

                stm.execute(s);
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.releaseConnection(conn);
            mutex.release();
        }
    }

    public List<Notificacao> lerNotificacoesServidor(){
        List<Notificacao> notificacoes = new ArrayList<>();
        Connection conn = ConnectionPool.getConnection();

        try {
            mutex.acquire();

            Statement stm = conn.createStatement();
                String sql = "SELECT * from Notificacao where direcionalidade = " + DirecionalidadeNotificacao.PARA_SERVIDOR.getValor() + ";";
                ResultSet rs = stm.executeQuery(sql);

                while (rs.next()) {
                    notificacoes.add(new Notificacao(rs.getInt("id"), rs.getInt("idRobo"), TipoNotificacao.getEnumByValor(rs.getInt("tipo"))));
                }

                // eliminar as notificações lidas - não elimina a mais devido ao lock
                String sql2 = "DELETE from Notificacao where direcionalidade = " + DirecionalidadeNotificacao.PARA_SERVIDOR.getValor() + ";";
                stm.executeUpdate(sql2);
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.releaseConnection(conn);
            mutex.release();
        }

        return notificacoes;
    }

    /**
     * Indica se um robo recebeu uma nova rota criada pelo sistema, apaga essa notificação pois foi lida
     * @param idRobo
     * @return
     */
    public boolean recebeuNovaRota(int idRobo) {
        Connection conn = ConnectionPool.getConnection();

        try {
            mutex.acquire();

            Statement stm = conn.createStatement();
                String sql = "SELECT * from Notificacao where idRobo=" + idRobo + " AND direcionalidade=" + DirecionalidadeNotificacao.PARA_ROBO.getValor() + ";";
                ResultSet rs = stm.executeQuery(sql);
                if (rs.next()) {
                    String sql2 = "DELETE from Notificacao where idRobo=" + idRobo + " AND direcionalidade=" + DirecionalidadeNotificacao.PARA_ROBO.getValor() + ";";
                    stm.executeUpdate(sql2);
                    return true;
                }

        } catch (InterruptedException | SQLException e){
            e.printStackTrace();
        } finally {
            ConnectionPool.releaseConnection(conn);
            mutex.release();
        }
        return false;
    }
}
