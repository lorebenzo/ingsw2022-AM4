package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.List;

public class CharacterIndexColorListsMsg extends SugarMessage {
    public final int characterIndex;
    public final List<Color> studentsToGet;
    public final List<Color> studentsToGive;


    public CharacterIndexColorListsMsg(int characterIndex, List<Color> studentsToGet, List<Color> studentsToGive) {
        super(SugarMethod.ACTION);

        this.characterIndex = characterIndex;
        this.studentsToGet = studentsToGet;
        this.studentsToGive = studentsToGive;
    }
}
