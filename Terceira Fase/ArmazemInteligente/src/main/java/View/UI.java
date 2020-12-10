
package View;

import java.util.Scanner;
import Model.Armazem.ArmazemLNFacade;
import Model.Armazem.Gestor.GestorDAO;
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
            System.out.println("Opção Inválida!!!");
            op = -1;
        }
        return op;
    }
    
    public void inicia() {  
        do {
            //showLogo();
            //verificaLogin();
            showMenu();
            switch ((opcao = getOpcao())) {
                case 1:
                    this.model.getPaletes();
                    break;
            }
        } while (opcao != 0);
        
        GestorDAO utilizadorDAO = new GestorDAO();
       
        System.out.println(utilizadorDAO.userExiste("teste"));
        
        System.out.println("Logging off, thank you for using ArmazémInteligente™ technologies.");
    }
}


    


