package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.StudentsNotInTheDiningRoomException;

public interface SchoolBoardCommonInterface {
    default void payCharacter(int cost){}
    default int getCoins(){return 0;}
    default void removeStudentFromDiningRoom(Color student) {}
}
