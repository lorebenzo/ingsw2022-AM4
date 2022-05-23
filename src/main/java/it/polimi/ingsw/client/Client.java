package it.polimi.ingsw.client;

import it.polimi.ingsw.server.server_logic.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        var gameClient = new GameClient();


        Thread t = new Thread(gameClient);

        t.start();


//        gameClient.login("mauro", "bro");
//        gameClient.signUp("player", "bro");


//        gameClient.parseLine("join-matchmaking --players=2 --expert=false");

        while(true) {
            var br = new BufferedReader(new InputStreamReader(System.in));
            gameClient.parseLine(br.readLine());
        }
    }

    public static class Server {
        public static void main(String[] args) throws IOException {
            var gameServer = new GameServer();

            Thread threadServer = new Thread(gameServer);
            threadServer.start();
        }
    }
}


