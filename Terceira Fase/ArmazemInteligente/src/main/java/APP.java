import Database.DBConnect;
import View.UI;


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
