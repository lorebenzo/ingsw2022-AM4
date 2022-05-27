package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class CharacterIdIslandMsg extends SugarMessage {
    public final int characterId;
    public final int archipelagoIslandCode;

    public CharacterIdIslandMsg(int characterId, int archipelagoIslandCode) {
        super(SugarMethod.ACTION);

        this.characterId = characterId;
        this.archipelagoIslandCode = archipelagoIslandCode;
    }
}
