package it.polimi.ingsw.messages;

public class InvalidNumberOfStepsMsg extends KOMsg{
    public InvalidNumberOfStepsMsg() {
        super("The inputed number of steps is invalid. " +
                "It has to be between 0 and the number of steps allowed by the card played in this round");
    }
}

