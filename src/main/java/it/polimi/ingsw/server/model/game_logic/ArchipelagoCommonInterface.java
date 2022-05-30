package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.ArchipelagoAlreadyLockedException;

public interface ArchipelagoCommonInterface {
    default void lock() throws ArchipelagoAlreadyLockedException { }
    default void unlock(){}
    default boolean isLocked(){ return false; }
    default void setTowersInfluence(boolean doTowersCount){ }
    default void setColorThatDoesntCount(Color color){ }
}
