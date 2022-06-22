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

    public int getCurrentCost() {
        return this.currentCost;
    }

    public void increaseCost(){
        this.currentCost++;
    }

    public int getCharacterId() {
        return characterId;
    }

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