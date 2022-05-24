package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
import it.polimi.ingsw.server.model.game_logic.LightGameState;

import java.util.Map;

/**
 * This is a special message that is not directly sent to the client, but it is instead intercepted by game controller
 * to notify to the clients the end of the game
 */
public class GameOverMsg extends SugarMessage{
    public final Map<Peer, Boolean> peerToIsWinner;
    public final LightGameState lightGameState;

    public GameOverMsg(Map<Peer, Boolean> peerToIsWinner, LightGameState lightGameState){
        super(SugarMethod.CONTROL_AND_NOTIFY);
        this.peerToIsWinner = peerToIsWinner;
        this.lightGameState = lightGameState;
    }
}
