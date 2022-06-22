package it.polimi.ingsw.server.controller.games_manager.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class PeerDisconnectedFromGameMsg extends SugarMessage {
    public PeerDisconnectedFromGameMsg(String jwt) {
        super(SugarMethod.NOTIFY, jwt);
    }
}
