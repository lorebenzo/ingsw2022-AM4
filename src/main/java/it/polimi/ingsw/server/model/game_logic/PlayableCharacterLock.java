package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Character;

public class PlayableCharacterLock extends PlayableCharacter{
    int availableLocks;
    protected PlayableCharacterLock(Character character) {
        super(character);
        this.availableLocks = 4;
    }

    @Override
    public boolean isLockAvailable() {
        return this.availableLocks > 0;
    }

    @Override
    public void unLock() {
        this.availableLocks++;
    }

    @Override
    public void useLock() {
        this.availableLocks--;
    }
}
