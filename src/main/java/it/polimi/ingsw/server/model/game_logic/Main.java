package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.exceptions.GameStateInitializationFailureException;

public class Main {
    public static void main(String[] args) {
        try {
            GameState gs = new GameState(2);
            gs.lightify();
        } catch (GameStateInitializationFailureException e) {
            e.printStackTrace();
        }

    }
}
