package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class CharacterIndexMsg extends SugarMessage {
    public final int characterIndex;

    public CharacterIndexMsg(int characterIndex, String jwt) {
        super(SugarMethod.ACTION, jwt);

        this.characterIndex = characterIndex;
    }
}
