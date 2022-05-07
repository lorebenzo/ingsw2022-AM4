package it.polimi.ingsw.server.controller.games_manager.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

import java.util.UUID;

public class JoinGameMsg extends SugarMessage {
    public final UUID roomId;

    public JoinGameMsg(UUID roomId) {
        super(SugarMethod.JOIN);
        this.roomId = roomId;
    }
}
