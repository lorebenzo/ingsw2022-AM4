package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class CharacterIndexArchipelagoMsg extends SugarMessage {
    public final int characterIndex;
    public final int archipelagoIslandCode;

    public CharacterIndexArchipelagoMsg(int characterIndex, int archipelagoIslandCode, String jwt) {
        super(SugarMethod.ACTION, jwt);

        this.characterIndex = characterIndex;
        this.archipelagoIslandCode = archipelagoIslandCode;
    }
}
