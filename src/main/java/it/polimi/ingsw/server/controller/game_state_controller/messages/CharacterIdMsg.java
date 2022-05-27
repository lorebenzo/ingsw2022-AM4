package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class CharacterIdMsg extends SugarMessage {
    public final int characterId;

    public CharacterIdMsg(int characterId) {
        super(SugarMethod.ACTION);

        this.characterId = characterId;
    }
}
