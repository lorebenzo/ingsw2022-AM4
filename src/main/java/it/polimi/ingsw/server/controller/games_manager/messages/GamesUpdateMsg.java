package it.polimi.ingsw.server.controller.games_manager.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

import java.util.UUID;

public class GamesUpdateMsg extends SugarMessage {
    public final UUID gameUUID;

     public GamesUpdateMsg(UUID gameUUID) {
         super(SugarMethod.NOTIFY);
         this.gameUUID = gameUUID;
     }
}
