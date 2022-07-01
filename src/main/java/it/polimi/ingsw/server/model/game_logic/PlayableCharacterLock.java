package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Character;

public class PlayableCharacterLock extends PlayableCharacter{
    private int availableLocks;

    protected PlayableCharacterLock(Character character) {
        super(character);
        this.availableLocks = 4;
    }

    /**
     * This method returns true if there are locks available, false otherwise
     * @return true if there are locks available, false otherwise
     */
    @Override
    public boolean isLockAvailable() {
        return this.availableLocks > 0;
    }

    /**
     * This method increases the lock count
     */
    @Override
    public void unLock() {
        this.availableLocks++;
    }

    /**
     * This method decreases the lock count
     */
    @Override
    public void useLock() {
        this.availableLocks--;
    }

    /**
     * This method returns the light version of a PlayableCharacterLock, containing all the useful information to be sent over the network
     * @return a LightPlayableCharacter containing all the useful information to be sent over the network
     */
    @Override
    public LightPlayableCharacter lightify() {
        return new LightPlayableCharacter(
                super.characterId,
                super.initialCost,
                super.currentCost,
                super.effect,
                this.availableLocks,
                null,
                null
        );
    }
}
