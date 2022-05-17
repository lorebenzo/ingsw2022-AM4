package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
import it.polimi.ingsw.server.model.game_logic.LightGameState;

public class UpdateClientMsg extends SugarMessage {
    public final LightGameState lightGameState;

    public UpdateClientMsg(LightGameState lightGameState) {
        super(SugarMethod.NOTIFY);
        this.lightGameState = lightGameState;
    }
}
