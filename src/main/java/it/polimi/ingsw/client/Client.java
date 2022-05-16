package it.polimi.ingsw.client;

import it.polimi.ingsw.server.server_logic.GameServer;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        /*
        var gameClient = new GameClient();
        var gameClient1 = new GameClient();

        Thread gc = new Thread(gameClient);
        gc.start();

        Thread gc1 = new Thread(gameClient1);
        gc1.start();

         */

        var gameServer = new GameServer();
        Thread gs = new Thread(gameServer);
        gs.start();



        // gameClient.parseLine("join-matchmaking --players=2 --expert=false");
        /*gameClient.parseLine("help");
        gameClient1.parseLine("join-matchmaking --players=2 --expert=false");

        gc.join();
        gc1.join();
        */
        gs.join();
    }
}
