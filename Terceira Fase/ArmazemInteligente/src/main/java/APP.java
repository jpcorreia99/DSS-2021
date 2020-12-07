import java.sql.*;

public class APP {
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.jdbc.Driver");
        } catch(ClassNotFoundException e){
            // Driver não disponível
        }
    }
}
