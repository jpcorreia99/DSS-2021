import Database.DBConnect;
import Database.RoboDAO;
import Model.Armazem.Robo.Robo;
import View.UI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;


public class APP {
    public static void main(String[] args) {
        try {
            DBConnect.setupBD();
            UI ui = new UI();
            ui.inicia();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problema com a ligação à base de dados");
        }
    }
}
