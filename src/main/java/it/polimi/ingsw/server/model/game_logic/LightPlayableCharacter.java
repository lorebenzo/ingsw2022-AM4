package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.List;
import java.util.Map;

public class LightPlayableCharacter {
    public final int characterId;
    public final int initialCost;
    public final int currentCost;
    public final String effect;
    public final Integer availableLocks;
    public final List<Color> students;
    public final Map<Color, Integer> professorToOriginalOwnerMap;

    public LightPlayableCharacter(int characterId, int initialCost, int currentCost, String effect, Integer availableLocks, List<Color> students, Map<Color, Integer> professorToOriginalOwnerMap) {
        this.characterId = characterId;
        this.initialCost = initialCost;
        this.currentCost = currentCost;
        this.effect = effect;
        this.availableLocks = availableLocks;
        this.students = students;
        this.professorToOriginalOwnerMap = professorToOriginalOwnerMap;
    }
}
