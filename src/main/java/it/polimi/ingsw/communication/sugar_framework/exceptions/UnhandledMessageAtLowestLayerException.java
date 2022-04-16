package it.polimi.ingsw.communication.sugar_framework.exceptions;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;

public class UnhandledMessageAtLowestLayerException extends Exception {
    public UnhandledMessageAtLowestLayerException(SugarMessage message) {
        super("Cannot handle message: " + message.serialize());
    }
}
