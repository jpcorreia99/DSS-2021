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

public class NotificacaoDAO {
    private static NotificacaoDAO singleton = null;
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

    public void enviarNotificacao(Notificacao notificacao, DirecionalidadeNotificacao direcionalidadeNotificacao){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            String s = "INSERT INTO Notificacao VALUES (" + notificacao.getId() +","+notificacao.getIdRobo() + ",'" +
                    notificacao.getTipo().getValor() + "')";
            stm.execute(s);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public List<Notificacao> lerNotificacoesServidor(){
        Connection conn = ConnectionPool.getConnection();
        List<Notificacao> notificacoes = new ArrayList<>();

        try (Statement stm = conn.createStatement()) {
            String sql = "SELECT * from Notificacao where direcionalidade = "+DirecionalidadeNotificacao.PARA_SERVIDOR +";";
            ResultSet rs = stm.executeQuery(sql);

            while(rs.next()) {
                notificacoes.add(new Notificacao(rs.getInt("id"),rs.getInt("idRobo"), TipoNotificacao.getEnumByValor(rs.getInt("tipo"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return notificacoes;
    }
}
