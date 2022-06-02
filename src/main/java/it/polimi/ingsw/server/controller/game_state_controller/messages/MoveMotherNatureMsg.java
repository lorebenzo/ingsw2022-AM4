package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class MoveMotherNatureMsg extends SugarMessage {
    public final int numberOfSteps;

    public MoveMotherNatureMsg(int numberOfSteps, String jwt) {
        super(SugarMethod.ACTION, jwt);
        this.numberOfSteps = numberOfSteps;
    }
}
