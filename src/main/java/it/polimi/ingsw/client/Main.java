package it.polimi.ingsw.client;

import it.polimi.ingsw.client.game_client_and_cli.GameClient;
import it.polimi.ingsw.client.new_gui.GUI;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        var br = new BufferedReader(new InputStreamReader(System.in));

        try {
            String input;
            do {
                System.out.println("Type 'gui' to start the graphic interface, type 'cli' to start the line interface ");
                input = br.readLine();
            } while (!(input.equalsIgnoreCase("cli") || input.equalsIgnoreCase("gui")));

            System.out.println("Application started");

            // Always start CLI
            GameClient gameClient = new GameClient();
            new Thread(gameClient).start();

            if(input.equals("gui")) {
                new Thread(() -> GUI.init(args, gameClient)).start();

                Platform.runLater(() -> GUI.switchView(GUI.View.LoginView));
            }

            // Handle user input
            boolean quit = false;
            while(!quit) {
                gameClient.parseLine(br.readLine());
            }
        } catch (IOException e) {
            System.out.println("Could not start the application, terminating...");
        }
    }
}
