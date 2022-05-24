package it.polimi.ingsw.server;

import com.google.gson.Gson;
import it.polimi.ingsw.server.server_logic.GameServer;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        GameServer gs = new GameServer();
        Thread t = new Thread(gs);

        t.start();
    }
}
