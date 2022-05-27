package it.polimi.ingsw.server.model.game_logic;

public interface Playable {
    default void unLock(){ }
    default void useLock(){ }
    default boolean isLockAvailable(){ return false; }


}
