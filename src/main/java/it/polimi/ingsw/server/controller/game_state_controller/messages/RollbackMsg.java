package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class RollbackMsg extends SugarMessage {
    public RollbackMsg(String jwt) {
        super(SugarMethod.CONTROL, jwt);
    }
}
