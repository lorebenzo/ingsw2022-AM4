package it.polimi.ingsw.server.controller.games_manager.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class ReJoinMsg extends SugarMessage {
    public ReJoinMsg(String jwt) {
        super(SugarMethod.CONTROL, jwt);
    }
}
