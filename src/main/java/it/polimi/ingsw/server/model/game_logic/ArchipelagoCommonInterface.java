package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.exceptions.ArchipelagoAlreadyLockedException;

public interface ArchipelagoCommonInterface {
    default void lock() throws ArchipelagoAlreadyLockedException { }
    default void unlock(){}
    default boolean isLocked(){ return false; }
    default void setIfTowersDoCount(boolean doTowersCount){ }
}
