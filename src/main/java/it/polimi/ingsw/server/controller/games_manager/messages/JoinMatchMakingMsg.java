package it.polimi.ingsw.server.controller.games_manager.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class JoinMatchMakingMsg extends SugarMessage {
    public final int numberOfPlayers;
    public final boolean expertMode;

    public JoinMatchMakingMsg(int numberOfPlayers, boolean expertMode) {
        super(SugarMethod.JOIN);
        this.numberOfPlayers = numberOfPlayers;
        this.expertMode = expertMode;
    }
}
