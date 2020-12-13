
package View;

import java.util.Scanner;
import Model.Armazem.ArmazemLNFacade;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


class MapaThread implements Runnable {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
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
                Map <Integer, List<Integer>> mapa = model.getMapa();
                
                System.out.print("\033[H\033[2J");
                System.out.flush();
                
                UI.showLogo();
                for (int i = 0; i < 12; i++) {
                    List<Integer> l = mapa.get(i);
                    System.out.print("                                                               ");
                    for (int j = 0; j < 16; j++) {
                        int bloco = l.get(j);

                        switch (bloco) {
                            case 0:
                                System.out.print(" ");
                                break;
                            case 1:
                                if ((i == 0 && j == 2) || (i == 2 && j == 0))
                                    System.out.print(ANSI_BLUE + "╔" + ANSI_RESET);
                                else if ((i == 2 && j == 2) || (i == 11 && j == 15))
                                    System.out.print(ANSI_BLUE +"╝" + ANSI_RESET);
                                else if ((i == 0 && j == 15) || (i == 9 && j == 2))
                                    System.out.print(ANSI_BLUE + "╗" + ANSI_RESET);
                                else if ((i == 9 && j == 0) || (i == 11 && j == 2))
                                    System.out.print(ANSI_BLUE + "╚" + ANSI_RESET);
                                else if (i == 0 || i == 11 || (i == 2 && j == 1) || (i == 9 && j == 1))
                                    System.out.print(ANSI_BLUE + "═");
                                else
                                    System.out.print(ANSI_BLUE + "║" + ANSI_RESET);
                                break;
                            case 2: 
                                System.out.print(ANSI_YELLOW + "☺" + ANSI_RESET);
                                break;
                            case 3: 
                                System.out.print(ANSI_GREEN + "☻" + ANSI_RESET);
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
                
                vezes++;
                System.out.println("\n                                                           Imprimi esta merda " + vezes + " vezes :)");
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
    private int opcao;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    
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
        System.out.println ("\n                                   Bemvindo ao programa etc pls don't forget to change this or Creissac kill you :)\n");
    }

    
    public void inicia() {
            showLogo();
            verificaLogin();
            showBoasVindas();
            
        do {
            
            showMenu();
           
            switch ((opcao = getOpcao())) {
                case 1:
                    showMapa();
                    break;
                case 2:
                    Object paletes = this.model.getPaletes();
                    showPaletes(paletes);
                    break;
            }
        } while (opcao != 0);
          
        System.out.println("                                      Logging off, thank you for using ArmazémInteligente™ technologies.");
    }
    
    public void showPaletes (Object paletes) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        showLogo();
        
        String[] materiais = {"Frangos", "Vibradores", "Tremoços", "Iogurtes", "Memes", "2ª fase de DSS", "Valérios", "Pinguins", "Esperanças", "Pantufas", "Caloiros"};
        Integer[] xs = {0, 7, 6, 4, 1, 8, 3, 2, 8, 2, 9};
        Integer[] ys = {7, 3, 3, 2, 9, 0, 0, 2, 3, 1, 11};
        Integer[] estado = {1, 2, 2, 1, 1, 0, 1, 1, 0, 1, 1};
      
        System.out.print(ANSI_CYAN + "                   --------------------------------------------------------------------------------------------");
        System.out.print("\n                   |                                      " + ANSI_RESET + String.format("%s", "Listagem de paletes") + ANSI_CYAN + "                                 |");
        System.out.print("\n                   --------------------------------------------------------------------------------------------");
        System.out.print("\n                   |"  + ANSI_RESET + "      Palete" + ANSI_CYAN + "      ||" + ANSI_RESET + "         Material     " + ANSI_CYAN + "    || " + ANSI_RESET + "   Coordenadas " + ANSI_CYAN + "   ||  " + ANSI_RESET + "     Estado     " + ANSI_CYAN + "   |");
        System.out.println("\n                   --------------------------------------------------------------------------------------------");
        
        for (int j = 0; j < 11; j++) {
            String s = null;
            
            switch (estado[j]) {
                case 0: 
                    s = ANSI_RED + "EM ESPERA" + ANSI_CYAN;
                    break;
                case 1:
                    s = ANSI_YELLOW + "EM TRANSPORTE" + ANSI_CYAN;
                    break;
                case 2:
                    s = ANSI_GREEN + "ARMAZENADA" + ANSI_CYAN;
                    break;
               
            }
            System.out.println("                   |" + ANSI_RESET + String.format("%9d%-9s", j, " ") + ANSI_CYAN + "|" +
            String.format("%-10s%-21s", "|", ANSI_RESET + materiais[j], " ") + ANSI_CYAN + "|" +  
            String.format("%-8s%-16s", "|", ANSI_RESET + "(" + xs[j].toString() + "," + ys[j].toString() + ")")  + ANSI_CYAN + "|" + 
            String.format("%-6s%-26s", "|", s, " ") + ANSI_CYAN + "|");
        }
            System.out.println(ANSI_CYAN + "                   --------------------------------------------------------------------------------------------\n" + ANSI_RESET);
        
        System.out.println ("\nPressione 'Enter' para voltar ao menu principal.");
        
        try {
            System.in.read();
        } catch (Exception e) {}
        
        System.out.print("\033[H\033[2J");
        System.out.flush();
        showLogo();
    }
    
    public static void showLogo () {
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
        
        try {
            System.in.read();
        } catch (Exception e) {}
        
        m.interrupt();
        
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
    }
}


    


