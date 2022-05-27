package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidNumberOfStepsException;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.List;

public interface GameStateCommonInterface {
    default void applyEffect(int characterId) {}
    default void lockArchipelago(Archipelago archipelagoToLock) throws ArchipelagoAlreadyLockedException{}

    default boolean wasCharacterPlayedInCurrentTurn() { return false; }
    default List<PlayableCharacter> getAvailableCharacters(){ return null; }
    default void unlockMotherNaturePosition(){}
    default void playCharacter(PlayableCharacter playableCharacter) throws InvalidCharacterIndexException {}
}