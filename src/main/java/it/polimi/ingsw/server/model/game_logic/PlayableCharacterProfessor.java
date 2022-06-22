package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.HashMap;
import java.util.Map;

public class PlayableCharacterProfessor extends PlayableCharacter{
    private final Map<Color, Integer> professorToOriginalOwnerMap;


    protected PlayableCharacterProfessor(Character character) {
        super(character);
        this.professorToOriginalOwnerMap = new HashMap<>();
    }

    @Override
    public Map<Color, Integer> getProfessorToOriginalOwnerMap() {
        return new HashMap<>(professorToOriginalOwnerMap);
    }

    @Override
    public void putProfessor(Color professor, int previousOwnerSchoolBoardId) {
        this.professorToOriginalOwnerMap.put(professor, previousOwnerSchoolBoardId);
    }

    @Override
    public void clearProfessorsToOriginalOwnerMap() {
        this.professorToOriginalOwnerMap.clear();
    }

    @Override
    public LightPlayableCharacter lightify() {
        return new LightPlayableCharacter(
                this.characterId,
                this.initialCost,
                this.currentCost,
                this.effect,
                null,
                null,
                this.professorToOriginalOwnerMap
        );
    }
}
