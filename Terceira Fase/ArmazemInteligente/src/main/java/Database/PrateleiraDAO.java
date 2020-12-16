package Database;

import Business.Armazem.Stock.EstadoPrateleira;
import Util.Tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static javax.swing.UIManager.getInt;

/** nota: quando o programa é reiniciado, prateleiras com boleano ocupado mas sem palete ficam livres */
public class PrateleiraDAO {
    private static PrateleiraDAO singleton = null;

    /**inse
     * Devolve uma instância do objeto
     * @return Instância
     */
    public static PrateleiraDAO getInstance() {
        if(singleton == null)
            singleton = new PrateleiraDAO();
        return PrateleiraDAO.singleton;
    }


    public void inserePaletes(List<Tuple<Integer,Integer>> tuplosPaletesArmazenadasZonas){
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            for(Tuple<Integer,Integer> tuploPaletePrateleira: tuplosPaletesArmazenadasZonas) {
                stm.executeUpdate("UPDATE Prateleira"+
                        " SET estado="+ EstadoPrateleira.OCUPADA.getValor()+
                        ",idPalete ="+tuploPaletePrateleira.getO()+
                        " WHERE id ="+tuploPaletePrateleira.getT()+";");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }
    }

    public int encontraPrateleiraLivre(){
        int idPrateleira = 0;
        Connection conn = ConnectionPool.getConnection();

        try (Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT * FROM Prateleira WHERE estado='"+ EstadoPrateleira.LIVRE.getValor()+"'");
            if (rs.next()) {  // Existe alguma prateleira livre
                idPrateleira = rs.getInt("id");
                //assinalar que agora a prateleira já não estará livre (mas ainda não tem palete, ou seja, está comprometida a receber uma"
                String query ="UPDATE Prateleira"+
                        " SET estado="+ EstadoPrateleira.COMPROMETIDA.getValor()+
                        " WHERE id ="+idPrateleira+";";
                stm.executeUpdate(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }finally {
            ConnectionPool.releaseConnection(conn);
        }

        return idPrateleira;
    }

//    /**
//     * Calcula o tamanho da estrutura de dados
//     * @return Tamanho
//     */
//    @Override
//    public int size() {
//        int i = 0;
//        try (Connection conn = DBConnect.connect()) {
//            Statement sta = conn.createStatement();
//            String sql = "SELECT count(*) AS Total FROM Prateleira";
//            ResultSet rs = sta.executeQuery(sql);
//            if(rs.next())
//                i = rs.getInt("Total");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return i;
//    }
//
//    /**
//     * Verifica se a estrutura de dados se encontra vazia
//     * @return True se estiver, false caso contrário
//     */
//    @Override
//    public boolean isEmpty() {
//        return size() == 0;
//    }
//
//    /**
//     * Verifica se a estrutura de dados contém uma Chave específica
//     * @param key Chave a procurar
//     * @return True se existir, false caso contrario
//     */
//    @Override
//    public boolean containsKey(Object key) {
//        try (Connection conn = DBConnect.connect()) {
//            Statement sta = conn.createStatement();
//            String sql = "SELECT * FROM Prateleira" +
//                    " WHERE id = " + key.toString();
//            ResultSet rs = sta.executeQuery(sql);
//            return rs.next();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * Verifica se a estrutura de dados contém um valor específico
//     * @param value Valor a procurar
//     * @return True se existir, false caso contrário
//     */
//    @Override
//    public boolean containsValue(Object value) {
//        Prateleira p = (Prateleira) value;
//        return containsKey(p.getId());
//    }
//
//    /**
//     * Obtém o valor da base de dados associado a uma dada chave
//     * @param key Chave a procurar
//     * @return O respetivo valor
//     */
//    @Override
//    public Prateleira get(Object key) {
//        Prateleira p = null;
//        try (Connection conn = DBConnect.connect()) {
//            Statement sta = conn.createStatement();
//            String sql = "SELECT * FROM Prateleira" +
//                    " WHERE id = " + key.toString();
//            ResultSet rs = sta.executeQuery(sql);
//            if(rs.next())
//                p = new Prateleira(rs.getInt(1),rs.getBoolean() , rs.getInt(2));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return p;
//    }
//
//    /**
//     * Acrescenta uma nova prateleira na base de dados
//     * @param key id da prateleira
//     * @param value prateleira
//     * @return null se não houver chave na base de dados, ou a prateleira que anteriormente teria essa chave
//     */
//    @Override
//    public Prateleira put(Integer key, Prateleira value) {
//        Prateleira p = null;
//        try (Connection conn = DBConnect.connect()) {
//            Statement stm = conn.createStatement();
//            if(containsKey(key))
//                p = get(key);
//            String sql = "INSERT INTO Prateleira VALUES (" +
//                    value.getId() + "," +
//                    value.getPaleteId() + ")" +
//                    "ON DUPLICATE KEY UPDATE paleteId = VALUES(paleteID);";
//            stm.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return p;
//    }




//    /**
//     * Remove uma prateleira da BD
//     * @param key Chave a remover
//     * @return Prateleira removida
//     */
//    @Override
//    public Prateleira remove(Object key) {
//        throw new NullPointerException("method not implemented!");
//    }
//
//    /**
//     * Insere todos os elementos do Mapa dado como argumento na BD
//     * @param m Estrutura com os dados a inserir
//     */
//    @Override
//    public void putAll(Map<? extends Integer, ? extends Prateleira> m) {
//        for(Prateleira p: m.values())
//            put(p.getId(),p);
//    }
//
//    /**
//     * Limpa a BD de todos os registos.
//     */
//    @Override
//    public void clear() {
//        try (Connection conn = DBConnect.connect()) {
//            Statement sta = conn.createStatement();
//            sta.executeUpdate("DELETE FROM Prateleira");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Apresenta um agrupamento de Chaves que pode ser percorrido
//     * @return Agrupamento com todas as chaves das prateleiras
//     */
//    @Override
//    public Set<Integer> keySet() {
//        Set<Integer> set = new HashSet<>();
//        try (Connection conn = DBConnect.connect()) {
//            Statement sta = conn.createStatement();
//            ResultSet rs = sta.executeQuery("SELECT id FROM Prateleira");
//            while(rs.next())
//                set.add(rs.getInt(1));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return set;
//    }
//
//    /**
//     * Devolve uma coleção com todos as Prateleira existentes na Base de Dados
//     * @return Coleção de todas as prateleiras
//     */
//    @Override
//    public Collection<Prateleira> values() {
//        Collection<Prateleira> col = new HashSet<>();
//        try (Connection conn = DBConnect.connect()) {
//            Statement sta = conn.createStatement();
//            ResultSet rs = sta.executeQuery("SELECT id FROM Prateleira");
//            while(rs.next())
//                col.add(this.get(rs.getInt(1)));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return col;
//    }
//
//    /**
//     * Devolve uma agrupamento com todos os pares id->Prateleira existentes na Base de Dados
//     * @return Coleção com toda a informação presente
//     */
//    @Override
//    public Set<Entry<Integer, Prateleira>> entrySet() {
//        Set<Entry<Integer,Prateleira>> res = new HashSet<>();
//        Entry<Integer,Prateleira> temp;
//        int id;
//        try (Connection conn = DBConnect.connect()) {
//            Statement sta = conn.createStatement();
//            ResultSet rs = sta.executeQuery("SELECT id FROM Prateleira");
//            while(rs.next()) {
//                id = rs.getInt(1);
//                temp = new MyEntry<>(id,this.get(id));
//                res.add(temp);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return res;
//    }

    final class MyEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
