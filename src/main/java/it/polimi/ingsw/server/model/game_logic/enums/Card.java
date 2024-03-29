package it.polimi.ingsw.server.model.game_logic.enums;

import java.util.Optional;

/**
 * This class represents each one of the 10 cards provided to each player at the start of a game.
 */
public enum Card implements Comparable<Card>{
    DOG(1, 1),
    GOOSE(2, 1),
    CAT(3, 2),
    PARROT(4, 2),
    FOX(5, 3),
    LIZARD(6, 3),
    OCTOPUS(7, 4),
    MASTIFF(8, 4),
    ELEPHANT(9, 5),
    TURTLE(10, 5);

    private final int value;
    private final int steps;

    Card(int value, int steps) {
        this.value = value;
        this.steps = steps;
    }

    public int getValue() {
        return value;
    }

    public int getSteps() {
        return steps;
    }

    public static Optional<Card> fromValue(int value) {
        for(var card : Card.values())
            if(card.value == value) return Optional.of(card);
        return Optional.empty();
    }

}
