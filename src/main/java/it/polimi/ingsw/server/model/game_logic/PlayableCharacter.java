package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Character;

public class PlayableCharacter implements Playable{
    private int characterId;
    private int initialCost;
    private int currentCost;
    private String effect;

    protected PlayableCharacter(Character character) {
        this.characterId = character.characterId;
        this.initialCost = character.initialCost;
        this.currentCost = character.initialCost;
        this.effect = character.effect;
    }
    public static PlayableCharacter initializeCharacter(Character character){
        if(!character.isStateful)
            return new PlayableCharacter(character);
        else
            return new PlayableCharacterWithStudents(character);
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
}