
package View;

import java.util.*;

import Business.Armazem.ArmazemLNFacade;

import Util.Tuple;
import java.util.ArrayList;


import java.util.concurrent.atomic.AtomicBoolean;


class MapaThread implements Runnable {
    ArmazemLNFacade model;
    private AtomicBoolean running;
    
    public MapaThread (ArmazemLNFacade model) {
        this.model = model;
        this.running = new AtomicBoolean(true);
    }
    
    public void interrupt () {
        running.set(false);
    }
    
    public void run() {
        int vezes = 0;
        while (running.get()) {
            try {
                int[][] mapa = model.getMapa();
                
                System.out.print("\033[H\033[2J");
                System.out.flush();
                
                UI.showLogo();
                
                for (int i = 0; i < 12; i++) {
                    for (int j = 0; j < 16; j++)
                        System.out.print(mapa[i][j]);
                    System.out.print("\n");
                }
                
                for (int i = 0; i < 12; i++) {
                    System.out.print("                                                               ");
                    for (int j = 0; j < 16; j++) {
                        switch (mapa[i][j]) {
                            case 0:
                                System.out.print(" ");
                                break;
                            case 1:
                                if ((i == 0 && j == 2) || (i == 2 && j == 0))
                                    System.out.print(UI.ANSI_BLUE + "╔" + UI.ANSI_RESET);
                                else if ((i == 2 && j == 2) || (i == 11 && j == 15))
                                    System.out.print(UI.ANSI_BLUE +"╝" + UI.ANSI_RESET);
                                else if ((i == 0 && j == 15) || (i == 9 && j == 2))
                                    System.out.print(UI.ANSI_BLUE + "╗" + UI.ANSI_RESET);
                                else if ((i == 9 && j == 0) || (i == 11 && j == 2))
                                    System.out.print(UI.ANSI_BLUE + "╚" + UI.ANSI_RESET);
                                else if (i == 0 || i == 11 || (i == 2 && j == 1) || (i == 9 && j == 1))
                                    System.out.print(UI.ANSI_BLUE + "═");
                                else
                                    System.out.print(UI.ANSI_BLUE + "║" + UI.ANSI_RESET);
                                break;
                            case 2: 
                                System.out.print(UI.ANSI_YELLOW + "☺" + UI.ANSI_RESET);
                                break;
                            case 3: 
                                System.out.print(UI.ANSI_GREEN + "☻" + UI.ANSI_RESET);
                                break;
                            case 4: 
                                System.out.print(UI.ANSI_BLUE + "○" + UI.ANSI_RESET);
                                break;
                            case 5: 
                                System.out.print(UI.ANSI_RED + "◙" + UI.ANSI_RESET);
                                break;
                        }
                    }
                    System.out.println();
                }
                
                vezes++;
                System.out.println("\n                                                           Imprimi esta merda " + vezes + " vezes :)");
                System.out.println("\nPressione 'Enter' para voltar ao menu principal.\n");
                Thread.sleep(1500);
                
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
        Thread.currentThread().interrupt();
    }
    
}


public class UI {
    private ArmazemLNFacade model;
    private Scanner scan;
    private List<String> opcoes;
    private List<MenuHandler> handlers;
    private int opcao;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    public interface MenuHandler {
        void execute();
    }
    
    public UI() {
        this.model = new ArmazemLNFacade();
        this.scan = new Scanner(System.in);
        this.opcoes = Arrays.asList("1. Ver Mapa do Armazém em tempo real",
                "2. Consultar localização de todas as paletes em armazém",
                "0. Sair");
        this.opcao = 0;
        this.handlers = new ArrayList<>();
        
        addHandler(() -> showMapa());
        addHandler(() -> showPaletes());
    }
    
    private void addHandler(UI.MenuHandler h) {
        this.handlers.add(h);
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
        showLogo();
        System.out.println ("\n                           Bemvindo ao programa etc pls don't forget to change this or Creissac kill you :)\n");
    }
    
    public void inicia() {
        this.model.start();
        showLogo();
        verificaLogin();
        showBoasVindas();
            
        do {
            showMenu();
           
            if ((opcao = getOpcao()) > 0 && opcao < 3)
                this.handlers.get(opcao-1).execute();
            else
                System.out.println("Opcao não disponivel.\n Pressione 'Enter' para continuar.\n");
        } while (opcao != 0);
        this.model.desligaSistema();
        exitScreen();
    }
    
    
    public void showPaletes () {
        Map <Integer, Tuple <String, Integer>> paletes = this.model.getPaletes();
        
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        showLogo();
      
        System.out.print(ANSI_CYAN + "                   --------------------------------------------------------------------------------------------");
        System.out.print("\n                   |                                      " + ANSI_RESET + String.format("%s", "Listagem de paletes") + ANSI_CYAN + "                                 |");
        System.out.print("\n                   --------------------------------------------------------------------------------------------");
        System.out.print("\n                   |"  + ANSI_RESET + "      Palete" + ANSI_CYAN + "      ||" + ANSI_RESET + "         Material     " + ANSI_CYAN + "    || " + ANSI_RESET + "   Coordenadas " + ANSI_CYAN + "   ||  " + ANSI_RESET + "     Estado     " + ANSI_CYAN + "   |");
        System.out.println("\n                   --------------------------------------------------------------------------------------------");
        
        for (Map.Entry<Integer, Tuple<String, Integer>> e : paletes.entrySet()) {
            String s = null;
            Tuple <String, Integer> t = e.getValue();
            switch (t.getT()) {
                case 1: 
                    s = ANSI_RED + "EM ESPERA" + ANSI_CYAN;
                    break;
                case 2:
                    s = ANSI_YELLOW + "EM TRANSPORTE" + ANSI_CYAN;
                    break;
                case 3:
                    s = ANSI_GREEN + "ARMAZENADA" + ANSI_CYAN;
                    break;
                case 4: 
                    s = ANSI_BLUE + "RECEM CHEGADA" + ANSI_BLUE;
                    break;
            }
            
            System.out.println("                   |" + ANSI_RESET + String.format("%9d%-9s", e.getKey(), " ") + ANSI_CYAN + "|" +
            String.format("%-10s%-21s", "|", ANSI_RESET + t.getO(), " ") + ANSI_CYAN + "|" +  
            String.format("%-8s%-16s", "|", ANSI_RESET + "(" + 0 + "," + 0 + ")")  + ANSI_CYAN + "|" + 
            String.format("%-6s%-26s", "|", s, " ") + ANSI_CYAN + "|");
        }
            System.out.println(ANSI_CYAN + "                   --------------------------------------------------------------------------------------------\n" + ANSI_RESET);
        
        premirTecla ();
        
        System.out.print("\033[H\033[2J");
        System.out.flush();
        showLogo();
    }
    
    public static void premirTecla () {
        System.out.println ("\nPressione 'Enter' para voltar ao menu principal.");
        
        try {
            System.in.read();
        } catch (Exception e) {}
    }
    
    public void exitScreen () {
        showLogo();
        System.out.println("                                 \n\n     Logging off, thank you for using ArmazémInteligente™ technologies.\n\n");
        System.exit(0);
    }
    
    public static void showLogo () {
        
        System.out.print("\033[H\033[2J");
        System.out.flush();        
        System.out.println(ANSI_GREEN + "                                                                   _____ " + ANSI_GREEN + "      _       _ _                  _       \n" +
ANSI_RED + "                    /\\                                 " + ANSI_GREEN + "           |_   _|     | |     | (_)                | |      \n" +
ANSI_RED + "                   /  \\   _ __ _ __ ___   __ _ _______ _ __ ___  " + ANSI_GREEN + "   | |  _ __ | |_ ___| |_  __ _  ___ _ __ | |_ ___ \n" +
ANSI_RED + "                  / /\\ \\ | '__| '_ ` _ \\ / _` |_  / _ \\ '_ ` _ \\   " + ANSI_GREEN + " | | | '_ \\| __/ _ \\ | |/ _` |/ _ \\ '_ \\| __/ _ \\\n" +
ANSI_RED + "                 / ____ \\| |  | | | | | | (_| |/ /  __/ | | | | | " + ANSI_GREEN + " _| |_| | | | ||  __/ | | (_| |  __/ | | | ||  __/\n" +
ANSI_RED + "                /_/    \\_\\_|  |_| |_| |_|\\__,_/___\\___|_| |_| |_|" + ANSI_GREEN + " |_____|_| |_|\\__\\___|_|_|\\__, |\\___|_| |_|\\__\\___|\n" +
ANSI_GREEN + "                                                                                            __/ |                   \n" +
ANSI_GREEN + "                                                                                           |___/                    \n" +
"" + ANSI_RESET);
    }
    
    public void showMapa () {
        MapaThread m;
        Thread t = new Thread ((m = new MapaThread(model)));
        t.start();
        
        UI.premirTecla();
                
        m.interrupt();
        
        showLogo();
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
                    showLogo();
                    System.out.println("Os dados que inseriu não são válidos, tente novamente.\n" 
                            + "Tentativas remanescentes: " + (3 - tentativas));
                } else {
                    showLogo();
                    System.out.println("Excedeu o número de tentativas permitidas, contacte o admnistrador para reaver "
                            + "acesso à aplicação. \n\n Até breve.\n");
                    System.exit(0);
                }
            }
        }
    }
}


    


