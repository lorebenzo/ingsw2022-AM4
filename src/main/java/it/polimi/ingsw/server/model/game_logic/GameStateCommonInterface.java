package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.MoveNotAvailableException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.List;
import java.util.Map;

public interface GameStateCommonInterface {

    default void unlockMotherNaturePosition(){}
    default boolean wasCharacterPlayedInCurrentTurn() { return false; }
    default List<PlayableCharacter> getAvailableCharacters(){ return null; }
    default void resetCharacterPlayedThisTurn() { }
    default void assignProfessorsAfterEffect(){ }
    default void setTowersInfluenceForAllArchipelagos(boolean doTowersCount){}
    default void resetColorThatDoesntCountForAllArchipelagos(){}
    default void refillCharacter() throws EmptyStudentSupplyException {}

    //No arguments
    default void playGetProfessorsWithSameStudents() throws MoveNotAvailableException, NotEnoughCoinsException { }
    default void playTwoAdditionalSteps() throws MoveNotAvailableException, NotEnoughCoinsException { }
    default void playTowersDontCount() throws MoveNotAvailableException, NotEnoughCoinsException { }
    default void playTwoAdditionalInfluence() throws MoveNotAvailableException, NotEnoughCoinsException { }

    //One argument
    default void playPutOneStudentFromCharacterToDiningRoom(Color color) throws StudentNotOnCharacterException, FullDiningRoomLaneException, MoveNotAvailableException, NotEnoughCoinsException {}
    default void playColorDoesntCount(Color color) throws MoveNotAvailableException, NotEnoughCoinsException { }
    default void playPutThreeStudentsInTheBag(Color color) throws MoveNotAvailableException, NotEnoughCoinsException, StudentsNotInTheDiningRoomException { }
    default void playCharacterLock(int archipelagoIslandCode) throws NoAvailableLockException, InvalidArchipelagoIdException, ArchipelagoAlreadyLockedException, MoveNotAvailableException, NotEnoughCoinsException {}
    default boolean playMoveMotherNatureToAnyArchipelago(int archipelagoIslandCode) throws InvalidArchipelagoIdException, MoveNotAvailableException, NotEnoughCoinsException { return  false; }

    //Two arguments
    default void playPutOneStudentFromCharacterToArchipelago(Color student, int archipelagoIslandCode) throws MoveNotAvailableException, InvalidArchipelagoIdException, StudentNotOnCharacterException, NotEnoughCoinsException {}
    default void playSwapThreeStudentsBetweenCharacterAndEntrance(List<Color> students1, List<Color> students2) throws InvalidStudentListsLengthException, MoveNotAvailableException, StudentNotOnCharacterException, StudentNotInTheEntranceException, NotEnoughCoinsException {}
    default void playSwapTwoStudentsBetweenEntranceAndDiningRoom(List<Color> students1, List<Color> students2) throws InvalidStudentListsLengthException, MoveNotAvailableException, StudentNotInTheEntranceException, FullDiningRoomLaneException, StudentsNotInTheDiningRoomException, NotEnoughCoinsException {}
}