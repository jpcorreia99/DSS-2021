import Business.Armazem.Robo.RoboFacade;
import Database.ConnectionPool;
import Database.DBConnect;
import Database.RoboDAO;
import Util.Coordenadas;
import View.UI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class APP {
    public static void main(String[] args) throws SQLException {
        try {
            DBConnect.setupBD();
            ConnectionPool.initialize();
            UI ui = new UI();
            ui.inicia();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problema com a ligação à base de dados");
        }

//        ConnectionPool.initialize();
//        RoboDAO roboDAO = RoboDAO.getInstance();
//        System.out.println(roboDAO.existeRoboDisponivel());

//        RoboDAO roboDAO = RoboDAO.getInstance();
//
//        long start = System.currentTimeMillis();
//        Robo r = roboDAO.get(1);
//        r = roboDAO.get(2);
//        r = roboDAO.get(3);
//        r = roboDAO.get(4);
//        long finish = System.currentTimeMillis();
//        long timeElapsed = finish - start;
//        System.out.println(timeElapsed);
//
//        long start2 = System.currentTimeMillis();
//        Set<Integer> roboIdList = new HashSet<>();
//        roboIdList.add(1);
//        roboIdList.add(2);
//        roboIdList.add(3);
//        roboIdList.add(4);
//
//
//
//        Map<Integer,Robo> map = roboDAO.getRobos(roboIdList);
//        long finish2 = System.currentTimeMillis();
//
//        for(Robo robo : map.values()){
//            System.out.println(robo.toString());
//        }
//        long timeElapsed2 = finish2 - start2;
//
//        System.out.println(timeElapsed2);
//

//        try {
//            ConnectionPool.initialize();
//            Connection conn = ConnectionPool.getConnection();
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//        RoboFacade roboFacade = new RoboFacade();
//
//        List<Coordenadas> percurso1 = new ArrayList<>();
//        Coordenadas a1 = new Coordenadas(1,1);
//        Coordenadas a2 = new Coordenadas(2,2);
//        Coordenadas a3 = new Coordenadas(3,3);
//        Coordenadas a4 = new Coordenadas(4,4);
//        percurso1.add(a1);
//        percurso1.add(a2);
//        percurso1.add(a3);
//        percurso1.add(a4);
//
//        List<Coordenadas> percurso2 = new ArrayList<>();
//        Coordenadas b1 = new Coordenadas(1,1);
//        Coordenadas b2 = new Coordenadas(2,2);
//        Coordenadas b3 = new Coordenadas(3,3);
//        Coordenadas b4 = new Coordenadas(5,5);
//        percurso2.add(b1);
//        percurso2.add(b2);
//        percurso2.add(b3);
//        percurso2.add(b4);
//
//        List<Coordenadas> percurso3 = new ArrayList<>();
//        Coordenadas c1 = new Coordenadas(1,1);
//        Coordenadas c2 = new Coordenadas(2,2);
//        Coordenadas c3 = new Coordenadas(3,3);
//        Coordenadas c4 = new Coordenadas(6,6);
//        percurso3.add(c1);
//        percurso3.add(c2);
//        percurso3.add(c3);
//        percurso3.add(c4);
//
//        List<Coordenadas> percurso4 = new ArrayList<>();
//        Coordenadas d1 = new Coordenadas(1,1);
//        Coordenadas d2 = new Coordenadas(2,2);
//        Coordenadas d3 = new Coordenadas(7,7);
//        percurso4.add(d1);
//        percurso4.add(d2);
//        percurso4.add(d3);
//
//        long start2 = System.currentTimeMillis();
//        roboFacade.transmiteInfoRota(1,1,percurso1);
//        roboFacade.transmiteInfoRota(2,2,percurso2);
//        roboFacade.transmiteInfoRota(3,3,percurso3);
//        roboFacade.transmiteInfoRota(4,4,percurso4);
//        long finish2 = System.currentTimeMillis();
//        long timeElapsed2 = finish2 - start2;
//
//        System.out.println(timeElapsed2);
//        start2 = System.currentTimeMillis();
//
//        long start3;
//        long finish3;
//        long timeElapsed3;
//        for(int i=0; i<6; i++){
//            start3 = System.currentTimeMillis();
//            roboFacade.moveRobos();
//            finish3 = System.currentTimeMillis();
//            timeElapsed3 = finish3-start3;
//            System.out.println(timeElapsed3);
//        }
//        finish2 = System.currentTimeMillis();
//        timeElapsed2 = finish2 - start2;
//        System.out.println(timeElapsed2);


    }
}
