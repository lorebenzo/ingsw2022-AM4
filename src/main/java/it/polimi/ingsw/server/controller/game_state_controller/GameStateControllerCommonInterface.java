package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.List;

public interface GameStateControllerCommonInterface {

    default void applyEffect(int characterIndex) throws WrongPhaseException, MoveAlreadyPlayedException, InvalidCharacterIndexException, MoveNotAvailableException, WrongArgumentsException, NotEnoughCoinsException {}
    default boolean applyEffect(int characterIndex, int archipelagoIslandCode) throws NotEnoughCoinsException, WrongPhaseException, InvalidCharacterIndexException, MoveAlreadyPlayedException, ArchipelagoAlreadyLockedException, InvalidArchipelagoIdException, NoAvailableLockException, MoveNotAvailableException, WrongArgumentsException, GameOverException { return false; }
    default void applyEffect(int characterIndex, Color student) throws InvalidCharacterIndexException, MoveAlreadyPlayedException, WrongPhaseException, MoveNotAvailableException, StudentNotOnCharacterException, FullDiningRoomLaneException, WrongArgumentsException, NotEnoughCoinsException, StudentsNotInTheDiningRoomException {}
    default void applyEffect(int characterIndex, Color student, int archipelagoIslandCode) throws InvalidCharacterIndexException, MoveAlreadyPlayedException, WrongPhaseException, MoveNotAvailableException, InvalidArchipelagoIdException, StudentNotOnCharacterException, WrongArgumentsException, NotEnoughCoinsException {}
    default void applyEffect(int characterIndex, List<Color> getStudents, List<Color> putStudents) throws InvalidCharacterIndexException, MoveAlreadyPlayedException, WrongPhaseException, WrongArgumentsException, InvalidStudentListsLengthException, StudentNotInTheEntranceException, StudentNotOnCharacterException, MoveNotAvailableException, StudentsNotInTheDiningRoomException, FullDiningRoomLaneException, NotEnoughCoinsException {}



}
