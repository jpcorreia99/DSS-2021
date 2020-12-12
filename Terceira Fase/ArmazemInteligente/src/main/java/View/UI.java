
package View;

import java.util.Scanner;
import Model.Armazem.ArmazemLNFacade;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

public class UI {
    private ArmazemLNFacade model;
    private Scanner scan;
    private List<String> opcoes;
    private int opcao;
   
    public UI() {
        this.model = new ArmazemLNFacade();
        this.scan = new Scanner(System.in);
        this.opcoes = Arrays.asList("1. Ver Mapa do Armazém em tempo real",
                "2. Consultar localização de todas as paletes em armazém",
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

    
    public void inicia() {
            showLogo();
            verificaLogin();
            showBoasVindas();
            
        do {
            
            showMenu();
           
            switch ((opcao = getOpcao())) {
                case 1:
//                    showMapa(this.model.getMapa());
                    break;
                case 2:
                    this.model.getPaletes();
                    break;
            }
        } while (opcao != 0);
          
        System.out.println("Logging off, thank you for using ArmazémInteligente™ technologies.");
    }
    
    public void showLogo () {
        System.out.println("                                                    _____       _       _ _                  _       \n" +
"     /\\                                            |_   _|     | |     | (_)                | |      \n" +
"    /  \\   _ __ _ __ ___   __ _ _______ _ __ ___     | |  _ __ | |_ ___| |_  __ _  ___ _ __ | |_ ___ \n" +
"   / /\\ \\ | '__| '_ ` _ \\ / _` |_  / _ \\ '_ ` _ \\    | | | '_ \\| __/ _ \\ | |/ _` |/ _ \\ '_ \\| __/ _ \\\n" +
"  / ____ \\| |  | | | | | | (_| |/ /  __/ | | | | |  _| |_| | | | ||  __/ | | (_| |  __/ | | | ||  __/\n" +
" /_/    \\_\\_|  |_| |_| |_|\\__,_/___\\___|_| |_| |_| |_____|_| |_|\\__\\___|_|_|\\__, |\\___|_| |_|\\__\\___|\n" +
"                                                                             __/ |                   \n" +
"                                                                            |___/                    \n" +
"");
    }
    
    
    public void showMapa (Map <Integer, List<Integer>> mapa) {
        for (int i = 0; i < 12; i++) {
            List<Integer> l = mapa.get(i);
    
            for (int j = 0; j < 16; j++) {
                int bloco = l.get(j);
                
                switch (bloco) {
                    case 0:
                        System.out.print(" ");
                        break;
                    case 1:
                        if ((i == 0 && j == 2) || (i == 2 && j == 0))
                            System.out.print("╔");
                        else if ((i == 2 && j == 2) || (i == 11 && j == 15))
                            System.out.print("╝");
                        else if ((i == 0 && j == 15) || (i == 9 && j == 2))
                            System.out.print("╗");
                        else if ((i == 9 && j == 0) || (i == 11 && j == 2))
                            System.out.print("╚");
                        else if (i == 0 || i == 11 || (i == 2 && j == 1) || (i == 9 && j == 1))
                            System.out.print("═");
                        else
                            System.out.print("║");
                        break;
                    case 2: 
                        System.out.print("☺");
                        break;
                    case 3: 
                        System.out.print("☻");
                        break;
                    case 4: 
                        System.out.print("○");
                        break;
                    case 5: 
                        System.out.print("◙");
                        break;
                }
            }
            System.out.println();
        }
    }
    public void verificaLogin () {
        String user = null;
        String password = null;
        boolean sucesso = false;
        int tentativas = 0;
        
        while (!sucesso) {
            try {
                System.out.println("\nInsira o seu nome:");
                user = scan.nextLine();
                System.out.println("Insira a sua password:");
                password = scan.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(e.toString());
            }

            sucesso = this.model.login(user, password);

            if (!sucesso) {
                if (++tentativas < 3) {
                    System.out.println("Os dados que inseriu não são válidos, tente novamente.\n" 
                            + "Tentativas remanescentes: " + (3 - tentativas));
                } else {
                    System.out.println("Excedeu o número de tentativas permitidas, contacte o admnistrador para reaver "
                            + "acesso à aplicação. Até breve.\n");
                    System.exit(0);
                }
            }
        }
        ProcessBuilder pb = new ProcessBuilder("clear");
        
        try {
            pb.start();
        } catch (Exception e) {}
    }
    
    public void clearScreen () {
        try {
            Runtime.getRuntime().exec("clear");
            } catch (Exception e) {}
    }
}


    


