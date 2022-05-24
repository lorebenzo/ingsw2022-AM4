package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli_graphics.Terminal;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.game_state_controller.messages.MoveMotherNatureMsg;
import it.polimi.ingsw.server.server_logic.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        var gameClient = new GameClient();
        Thread t = new Thread(gameClient);

        t.start();
        Thread.sleep(1000);
        gameClient.login("player", "bro");
        Thread.sleep(1000);
        gameClient.joinMatchMaking(2, false);

        while (true) {
            var br = new BufferedReader(new InputStreamReader(System.in));
            gameClient.parseLine(br.readLine());
        }
    }
}

class Server {
    public static void main(String[] args) throws IOException {
        GameServer gs = new GameServer();
        Thread t = new Thread(gs);

        t.start();
    }
}

