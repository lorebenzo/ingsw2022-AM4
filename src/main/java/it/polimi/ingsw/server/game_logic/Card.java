package it.polimi.ingsw.server.game_logic;

/**
 * This class represents each one of the 10 cards provided to each player at the start of a game.
 */
public class Card {
    private final int value;
    private final int steps;

    public Card(int value, int steps) {
        this.value = value;
        this.steps = steps;
    }

    public int getValue() {
        return value;
    }

    public int getSteps() {
        return steps;
    }
}
