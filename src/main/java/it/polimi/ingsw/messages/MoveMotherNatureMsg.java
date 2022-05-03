package it.polimi.ingsw.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class MoveMotherNatureMsg extends SugarMessage {
    public final int numberOfSteps;

    public MoveMotherNatureMsg(int numberOfSteps) {
        super(SugarMethod.ACTION);
        this.numberOfSteps = numberOfSteps;
    }
}
