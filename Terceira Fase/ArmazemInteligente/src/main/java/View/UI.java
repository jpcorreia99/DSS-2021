
package View;

import java.util.Scanner;
import Model.Armazem.ArmazemLNFacade;
import Model.Armazem.Gestor.Gestor;
import Model.Armazem.Gestor.GestorDAO;
import Model.DBConnect;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

public class UI {
    private ArmazemLNFacade model;
    private Scanner scan;
    private List<String> opcoes;
    private int opcao;
   
    public UI() {
        this.model = new ArmazemLNFacade();
        this.scan = new Scanner(System.in);
        this.opcoes = Arrays.asList("1. Consultar localização de todas as paletes em armazém",
                       "0. Sair");
        this.opcao = 0;
    }
    
    private void showMenu() {
        for (String s : opcoes)
            System.out.println(s);
    }
    
    private int getOpcao() {
        int op;
        
        try {
            op = scan.nextInt();
            System.out.println("Opção: " + op);
        } catch (InputMismatchException e) {
            op = -1;
            System.out.println(e.toString());
        }
        
        if (op<0 || op>this.opcoes.size()) {
            System.out.println("A opção selecionada não é válida.");
            op = -1;
        }
        return op;
    }
    
    public void showBoasVindas() {
        System.out.println ("\nBemvindo ao programa etc pls don't forget to change this or Creissac kill you :)\n");
    }
    
    public void populamentoInicialBD() throws SQLException {
        Connection conn = DBConnect.connect();
        Statement s = conn.createStatement();
        String[] gestores = {"tobias", "anacleto", "zeca"};
        String[] passwords = {"420noscope", "1234", "1234"};
        
        s.executeUpdate("CREATE DATABASE IF NOT EXISTS ArmazemInteligente;");
        s.execute("USE ArmazemInteligente;");
        s.execute("CREATE TABLE IF NOT EXISTS Gestores (username VARCHAR(45), password VARCHAR(45), PRIMARY KEY (username));");
        
        for (int i = 0; i < gestores.length; i++) {
            s.execute ("INSERT IGNORE INTO Gestores (username, password) VALUES ('" + gestores[i] + "', '" +
                    Gestor.generate(passwords[i]) + "');");
        }
    }
    
    public void inicia() {  
        try {
                populamentoInicialBD();
            } catch (SQLException e) {
                System.out.println (e.getMessage());
            }
            verificaLogin();
            showBoasVindas();
            
        do {
            //showLogo();
            showMenu();
            
            
            switch ((opcao = getOpcao())) {
                case 1:
                    this.model.getPaletes();
                    break;
            }
        } while (opcao != 0);
        
        
        
        System.out.println("Logging off, thank you for using ArmazémInteligente™ technologies.");
    }
    
    public void verificaLogin () {
        String user = null;
        String password = null;
        
        try {
            System.out.println("Insira o seu nome:");
            user = scan.nextLine();
            System.out.println("Insira a sua password:");
            password = scan.nextLine();
        } catch (InputMismatchException e) {
            System.out.println(e.toString());
        }
        
        if (this.model.login(user, password)) {
            System.out.println("O par " + user + " " + password + " é válido");
        } else 
            System.out.println("O par " + user + " " + password + " é inválido");
    }
    
    public void clearScreen () {
        try {
            Runtime.getRuntime().exec("clear");
            } catch (Exception e) {}
    }
}


    


