package it.polimi.ingsw.client;

import it.polimi.ingsw.server.server_logic.GameServer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ClientServerTests {

    @Test
    public void joinMatchMakingTest() {
        try {
            Thread t = new Thread(new GameServer());
            t.start();

            var gc0 = new GameClient();
            var gc1 = new GameClient();
            Thread q0 = new Thread(gc0);
            Thread q1 = new Thread(gc1);
            q0.start();
            q1.start();

            gc0.joinMatchMaking(2, true);
            gc1.joinMatchMaking(2, true);

            q0.join();
            q1.join();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
