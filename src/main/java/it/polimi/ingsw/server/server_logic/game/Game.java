package it.polimi.ingsw.server.server_logic.game;

import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.server.server_logic.Player;

import java.util.*;

public class Game extends SugarMessageProcessor {
    public final UUID roomId;
    private final List<Player> players = new LinkedList<>();
    private final Map<UUID, Integer> upiToSchoolBoardId = new HashMap<>();

    public Game(UUID roomId) {
        this.roomId = roomId;
    }

    public void addPlayer(Player player) {
        synchronized (this.players) {
            this.players.add(player);
        }
    }

    private void startGame() {
        synchronized (this.players) {
            for (int i = 0; i < players.size(); i++)
                upiToSchoolBoardId.put(players.get(i).associatedPeer.upi, i);
        }
    }


}
