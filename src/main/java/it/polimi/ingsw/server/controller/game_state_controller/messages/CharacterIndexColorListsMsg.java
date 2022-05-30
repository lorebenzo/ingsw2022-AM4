package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.List;

public class CharacterIndexColorListsMsg extends SugarMessage {
    public final int characterIndex;
    public final List<Color> students1;
    public final List<Color> students2;


    public CharacterIndexColorListsMsg(int characterIndex, List<Color> students1, List<Color> students2) {
        super(SugarMethod.ACTION);

        this.characterIndex = characterIndex;
        this.students1 = students1;
        this.students2 = students2;
    }
}
