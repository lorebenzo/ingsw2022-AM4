package it.polimi.ingsw.server.controller.game_controller;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;


public class GameControllerTest {

    @Test
    public void testActivePlayers() {
        try {
            GameController gameController = new GameController(UUID.randomUUID(), false);

            var firstPlayerPeer = new Peer(new Socket());
            gameController.addPlayer(new Player(firstPlayerPeer, "user1"));
            gameController.addPlayer(new Player(new Peer(new Socket()), "user2"));

            // It contains user1, and the relative peer
            assertTrue(gameController.containsPlayer("user1"));
            assertTrue(gameController.containsPeer(firstPlayerPeer));

            assertFalse(gameController.containsPlayer("user4"));
            assertFalse(gameController.containsPeer(null));

            // Get the peer from the username
            assertEquals(gameController.getPeerFromPlayer("user1").get(), firstPlayerPeer);
            assertNotEquals(gameController.getPeerFromPlayer("user2").get(), firstPlayerPeer);
            assertTrue(gameController.getPeerFromPlayer("nouser").isEmpty());

            // Assert the active players be 2
            assertEquals(gameController.activePlayers(), 2);

            // Remove one player, active players decreased to 1
            gameController.setInactivePlayer(firstPlayerPeer);
            assertEquals(gameController.activePlayers(), 1);

            // Remove the player that I have already removed, it should not impact che active players
            gameController.setInactivePlayer(firstPlayerPeer);
            assertEquals(gameController.activePlayers(), 1);


            // The players size remains unchanged
            assertEquals(gameController.getPlayers().size(), 2);


            // The players contained are user1 and user2
            assertTrue(gameController.getPlayers().stream()
                    .anyMatch(player -> player.username.equals("user1"))
            );

            assertTrue(gameController.getPlayers().stream()
                    .anyMatch(player -> player.username.equals("user2"))
            );

            gameController.removePlayer("user1");
            // Players size is now 1
            assertEquals(gameController.getPlayers().size(), 1);
            // Active players is now 1
            assertEquals(gameController.activePlayers(), 1);

        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void testUpdatePeerIfOlder() {
        GameController gameController = new GameController(UUID.randomUUID(), false);

        gameController.addPlayer(new Player(new Peer(new Socket()), "user1"));
        gameController.addPlayer(new Player(new Peer(new Socket()), "user2"));
        var changedPeer = new Peer(new Socket());

        // Tries to update the old peer
        gameController.updatePeerIfOlder("user1", changedPeer);


        assertEquals(gameController.getPeerFromPlayer("user1").get(), changedPeer);

        assertThrows(IllegalArgumentException.class, () -> gameController.updatePeerIfOlder(null, null));
    }
}
