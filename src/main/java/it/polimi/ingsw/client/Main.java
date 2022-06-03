//package it.polimi.ingsw.client;
//
//import it.polimi.ingsw.client.game_client_and_cli.GameClient;
//import it.polimi.ingsw.client.gui.App;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//public class Main {
//    public static void main(String[] args) {
//        var br = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("Press enter to start GUI mode, type 'cli' to start CLI mode: ");
//
//        try {
//            var input = br.readLine();
//
//            System.out.println("Application started");
//
//            // Always start CLI
//            GameClient gameClient = new GameClient();
//            new Thread(gameClient).start();
//
//            if(!input.equals("cli")) {
//                // Start gui
//                App.main(new String[0]);
//            }
//
//            // Handle user input
//            boolean quit = false;
//            while(!quit) {
//                gameClient.parseLine(br.readLine());
//            }
//        } catch (IOException e) {
//            System.out.println("Could not start the application, terminating...");
//        }
//    }
//}
