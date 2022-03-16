package it.polimi.ingsw.server.game_logic;

/**
 * This class represents each one of the 10 cards provided to each player at the start of a game.
 */
public class Card {
    public enum CardType {
        DOG, GOOSE, CAT, PARROT, FOX, LIZARD, OCTOPUS, MASTIFF, ELEPHANT, TURTLE
    }

    private final int value;
    private final int steps;
    private final CardType cardType;

    public Card(int value, int steps, CardType cardType) {
        this.value = value;
        this.steps = steps;
        this.cardType = cardType;
    }

    public int getValue() {
        return value;
    }
    public int getSteps() {
        return steps;
    }
    public CardType getCardType() {
        return cardType;
    }
}
