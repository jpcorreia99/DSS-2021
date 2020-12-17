import Database.ConnectionPool;
import Database.DBConnect;
import Database.RotaDAO;
import Util.Coordenadas;
import View.UI;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class APP {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
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
//        DBConnect.setupBD();
//        ConnectionPool.initialize();
//        List<Coordenadas> percurso3 = new ArrayList<>();
//        Coordenadas c1 = new Coordenadas(1,1);
//        Coordenadas c2 = new Coordenadas(2,2);
//        Coordenadas c3 = new Coordenadas(3,3);
//        Coordenadas c4 = new Coordenadas(6,6);
//        percurso3.add(c1);
//        percurso3.add(c2);
//        percurso3.add(c3);
//        percurso3.add(c4);
////
//        List<Coordenadas> percurso4 = new ArrayList<>();
//        Coordenadas d1 = new Coordenadas(1,1);
//        Coordenadas d2 = new Coordenadas(2,2);
//        Coordenadas d3 = new Coordenadas(3,1);
//        Coordenadas d4 = new Coordenadas(9,0);
//        Coordenadas d5 = new Coordenadas(7,7);
//        percurso4.add(d1);
//        percurso4.add(d2);
//        percurso4.add(d3);
//        percurso4.add(d4);
//        percurso4.add(d5);
//
//        Map<Integer,List<Coordenadas>> tlwp = new HashMap<>();
//        tlwp.put(3,percurso3);
//        tlwp.put(4,percurso4);
//
//        RotaDAO rotaDAO = new RotaDAO();
//        rotaDAO.adicionaRota(3,percurso3);
//        rotaDAO.adicionaRota(4,percurso4);
//
//        while(!rotaDAO.rotaTerminou(3)){
//            System.out.println(rotaDAO.getProximoPasso(3).toString());
//        }

//        String[] parts = coordenadasCodificadas.split("/");
//        for(String part : parts){
//            System.out.println(".");
//            System.out.println(part);
//        }
//
//        List<Coordenadas> res = rotasDAO.get(coordenadasCodificadas);
//        for(Coordenadas cord : res){
//            System.out.println(cord.toString());
//        }
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
