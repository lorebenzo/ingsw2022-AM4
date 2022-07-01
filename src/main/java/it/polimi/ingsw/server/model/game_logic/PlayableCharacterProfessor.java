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

    /**
     * This method returns the map that maps every professor to the original owner, prior the activation of this character
     * @return the map that maps every professor to the original owner, prior the activation of this character
     */
    @Override
    public Map<Color, Integer> getProfessorToOriginalOwnerMap() {
        return new HashMap<>(professorToOriginalOwnerMap);
    }

    /**
     * This method adds a professor to the map that links every professor to its owner prior the activation of the character
     * @param professor is the color representing the professor that has to be added to the map
     * @param previousOwnerSchoolBoardId is the int representing the ID of the schoolBoard of the original owner
     */
    @Override
    public void putProfessor(Color professor, int previousOwnerSchoolBoardId) {
        this.professorToOriginalOwnerMap.put(professor, previousOwnerSchoolBoardId);
    }

    /**
     * This method clears the map after the effect of the character is finished
     */
    @Override
    public void clearProfessorsToOriginalOwnerMap() {
        this.professorToOriginalOwnerMap.clear();
    }

    /**
     * This method returns the light version of the PlayableCharacter, containing all the useful information that need to be sent
     * over the network
     * @return a LightPlayableCharacter containing all the useful information that need to be sent over the network
     */
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
