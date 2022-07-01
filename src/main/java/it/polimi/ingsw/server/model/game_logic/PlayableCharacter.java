package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Character;

public class PlayableCharacter implements Playable{
    protected final int characterId;
    protected final int initialCost;
    protected int currentCost;
    protected final String effect;

    protected PlayableCharacter(Character character) {
        this.characterId = character.characterId;
        this.initialCost = character.initialCost;
        this.currentCost = character.initialCost;
        this.effect = character.effect;
    }

    /**
     * This method creates the right PlayableCharacter based on the inputted Character and returns it
     * @param character represents the character that has to be created
     * @return a PlayableCharacter based on the inputted Character
     */
    public static PlayableCharacter createCharacter(Character character){
        if(character == Character.LOCK_ARCHIPELAGO)
            return new PlayableCharacterLock(character);
        else if(character == Character.GET_PROFESSORS_WITH_SAME_STUDENTS)
            return new PlayableCharacterProfessor(character);
        else if(character == Character.PUT_ONE_STUDENT_FROM_CHARACTER_TO_ARCHIPELAGO ||
                character == Character.SWAP_THREE_STUDENTS_BETWEEN_CHARACTER_AND_ENTRANCE ||
                character == Character.PUT_ONE_STUDENT_FROM_CHARACTER_INTO_DINING_ROOM)
            return new PlayableCharacterWithStudents(character);
        else
            return new PlayableCharacter(character);
    }

    /**
     * This method returns the current cost to activate the character
     * @return an int representing the current cost of the character
     */
    public int getCurrentCost() {
        return this.currentCost;
    }

    /**
     * This method is used to increase the cost of the character after each activation
     */
    public void increaseCost(){
        this.currentCost++;
    }

    /**
     * This method returns the ID of the character
     * @return an int representing the ID of the character
     */
    public int getCharacterId() {
        return characterId;
    }

    /**
     * This method creates the light version of the PlayableCharacter containing all the useful information that need to be sent
     * over the network and returns it
     * @return a LightPlayableCharacter representing the light version of the PlayableCharacter
     */
    public LightPlayableCharacter lightify(){
        return new LightPlayableCharacter(
                this.characterId,
                this.initialCost,
                this.currentCost,
                this.effect,
                null,
                null,
                null
        );
    }
}