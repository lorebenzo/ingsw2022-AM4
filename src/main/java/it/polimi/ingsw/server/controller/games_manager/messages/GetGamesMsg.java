package it.polimi.ingsw.server.controller.games_manager.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class GetGamesMsg extends SugarMessage {
    public GetGamesMsg(String jwt) {
        super(SugarMethod.NOTIFY, jwt);
    }
}
