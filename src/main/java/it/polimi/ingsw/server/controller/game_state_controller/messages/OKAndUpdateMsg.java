package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;


public class OKAndUpdateMsg extends SugarMessage {

    public final OKMsg okMsg;
    public final UpdateClientMsg updateClientMsg;

    public OKAndUpdateMsg(OKMsg okMsg, UpdateClientMsg updateClientMsg) {
        super(SugarMethod.CONTROL_AND_NOTIFY);
        this.okMsg = okMsg;
        this.updateClientMsg = updateClientMsg;
    }
}
