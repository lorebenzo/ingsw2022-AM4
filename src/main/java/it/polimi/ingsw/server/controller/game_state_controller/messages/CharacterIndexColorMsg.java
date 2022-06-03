package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

public class CharacterIndexColorMsg extends SugarMessage {
    public final int characterIndex;
    public final Color color;

    public CharacterIndexColorMsg(int characterIndex, Color color, String jwt) {
        super(SugarMethod.ACTION, jwt);

        this.characterIndex = characterIndex;
        this.color = color;
    }
}
