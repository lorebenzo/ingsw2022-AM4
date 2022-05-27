package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.MoveAlreadyPlayedException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.MoveNotAvailableException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.WrongPhaseException;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.List;

public interface ControllerCommonInterface {

    default void applyEffect(int characterIndex) throws WrongPhaseException, MoveAlreadyPlayedException, InvalidCharacterIndexException, MoveNotAvailableException {}
    default boolean applyEffect(int characterIndex, int archipelagoIslandCode) throws NotEnoughCoinsException, WrongPhaseException, InvalidCharacterIndexException, MoveAlreadyPlayedException, ArchipelagoAlreadyLockedException, InvalidArchipelagoIdException, NoAvailableLockException, MoveNotAvailableException { return false; }
    default void applyEffect(int characterIndex, List<Color> student){}
    default void applyEffect(int characterIndex, List<Color> getStudents, List<Color> putStudents){}
    default void applyEffect(int characterIndex,int archipelagoIslandCodes, List<Color> students){}


}
