package Database;

import Business.Armazem.Stock.EstadoPalete;
import Business.Armazem.Stock.Palete;
import Util.DirecionalidadeNotificacao;
import Util.Notificacao;
import Util.TipoNotificacao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NotificacaoDAO {
    private static NotificacaoDAO singleton = null;

    private Lock lock;
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
        this.lock = new ReentrantLock();
    }

    public void enviarNotificacao(Notificacao notificacao, DirecionalidadeNotificacao direcionalidadeNotificacao){
        try {
            lock.lock();
            Connection conn = ConnectionPool.getConnection();

            try (Statement stm = conn.createStatement()) {
                String s = "INSERT INTO Notificacao (idRobo,tipo,direcionalidade) VALUES (" +
                        notificacao.getIdRobo() + ", " +
                        notificacao.getTipo().getValor() + ", " +
                        direcionalidadeNotificacao.getValor() + ")";

                stm.execute(s);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            } finally {
                ConnectionPool.releaseConnection(conn);
            }
        }finally {
            lock.unlock();
        }
    }

    public List<Notificacao> lerNotificacoesServidor(){
        try {
            lock.lock();
            Connection conn = ConnectionPool.getConnection();
            List<Notificacao> notificacoes = new ArrayList<>();

            try (Statement stm = conn.createStatement()) {
                String sql = "SELECT * from Notificacao where direcionalidade = " + DirecionalidadeNotificacao.PARA_SERVIDOR.getValor() + ";";
                ResultSet rs = stm.executeQuery(sql);

                while (rs.next()) {
                    notificacoes.add(new Notificacao(rs.getInt("id"), rs.getInt("idRobo"), TipoNotificacao.getEnumByValor(rs.getInt("tipo"))));
                }

                // eliminar as notificações lidas - não elimina a mais devido ao lock
                String sql2 = "DELETE from Notificacao where direcionalidade = " + DirecionalidadeNotificacao.PARA_SERVIDOR.getValor() + ";";
                stm.executeUpdate(sql2);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConnectionPool.releaseConnection(conn);
            }

            return notificacoes;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Indica se um robo recebeu uma nova rota criada pelo sistema, apaga essa notificação pois foi lida
     * @param idRobo
     * @return
     */
    public boolean recebeuNovaRota(int idRobo) {
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            String sql = "SELECT * from Notificacao where idRobo="+idRobo+" AND direcionalidade="+ DirecionalidadeNotificacao.PARA_ROBO.getValor()+";";
            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                String sql2 = "DELETE from Notificacao where idRobo="+idRobo+" AND direcionalidade="+ DirecionalidadeNotificacao.PARA_ROBO.getValor()+";";
                stm.executeUpdate(sql2);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return false;
    }
}
