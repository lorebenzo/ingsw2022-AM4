package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidNumberOfStepsException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.MoveNotAvailableException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ExpertGameStateTest {

    @Test
    public void playPutOneStudentFromCharacterToArchipelago() throws GameStateInitializationFailureException, EmptyStudentSupplyException, InvalidArchipelagoIdException, StudentNotOnCharacterException, MoveNotAvailableException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.PUT_ONE_STUDENT_FROM_CHARACTER_TO_ARCHIPELAGO))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        Color studentToMove = gameState.getAvailableCharacters().get(0).getStudents().get(0);

        gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove,0);
            if(gameState.getArchipelagoFromSingleIslandCode(0).isPresent())
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getStudents().contains(studentToMove));
        assertFalse(gameState.getAvailableCharacters().get(0).getStudents().isEmpty());

        studentToMove = gameState.getAvailableCharacters().get(0).getStudents().get(0);

        gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove,0);
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getStudents().contains(studentToMove));
        assertFalse(gameState.getAvailableCharacters().get(0).getStudents().isEmpty());

        studentToMove = gameState.getAvailableCharacters().get(0).getStudents().get(0);

        gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove,0);
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getStudents().contains(studentToMove));
        assertFalse(gameState.getAvailableCharacters().get(0).getStudents().isEmpty());

        studentToMove = gameState.getAvailableCharacters().get(0).getStudents().get(0);

        gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove,0);
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getStudents().contains(studentToMove));
        assertTrue(gameState.getAvailableCharacters().get(0).getStudents().isEmpty());

        Color studentToMove2 = studentToMove;

        assertThrows(StudentNotOnCharacterException.class, () -> gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove2,0));
    }

    @Test
    public void playGetProfessorsWithSameStudents2() throws GameStateInitializationFailureException, EmptyStudentSupplyException, MoveNotAvailableException, FullDiningRoomLaneException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.GET_PROFESSORS_WITH_SAME_STUDENTS))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.assignProfessor(Color.RED);
        gameState.assignProfessor(Color.GREEN);
        assertEquals(Set.of(Color.RED, Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.assignProfessor(Color.RED);
        gameState.assignProfessor(Color.GREEN);
        gameState.setCurrentPlayerSchoolBoardId(0);
        assertEquals(Set.of(Color.RED, Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.playGetProfessorsWithSameStudents();
        assertEquals(Set.of(Color.RED, Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());


        gameState.assignProfessorsAfterEffect();
        gameState.resetCharacterPlayedThisTurn();
        assertEquals(Set.of(),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
        gameState.setCurrentPlayerSchoolBoardId(0);
        assertEquals(Set.of(Color.RED, Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
    }

    @Test
    public void playGetProfessorsWithSameStudents3() throws GameStateInitializationFailureException, EmptyStudentSupplyException, MoveNotAvailableException, FullDiningRoomLaneException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(3,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.GET_PROFESSORS_WITH_SAME_STUDENTS))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.assignProfessor(Color.RED);
        assertEquals(Set.of(Color.RED),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.assignProfessor(Color.GREEN);
        assertEquals(Set.of(Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());


        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.assignProfessor(Color.RED);
        gameState.assignProfessor(Color.GREEN);
        gameState.setCurrentPlayerSchoolBoardId(0);
        assertEquals(Set.of(Color.RED),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
        gameState.setCurrentPlayerSchoolBoardId(1);
        assertEquals(Set.of(Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());


        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.playGetProfessorsWithSameStudents();
        assertEquals(Set.of(Color.RED, Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());


        gameState.assignProfessorsAfterEffect();
        gameState.resetCharacterPlayedThisTurn();
        assertEquals(Set.of(),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
        gameState.setCurrentPlayerSchoolBoardId(0);
        assertEquals(Set.of(Color.RED),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
        gameState.setCurrentPlayerSchoolBoardId(1);
        assertEquals(Set.of(Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
    }

    @Test
    public void playGetProfessorsWithSameStudents4() throws MoveNotAvailableException, FullDiningRoomLaneException, GameStateInitializationFailureException, EmptyStudentSupplyException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(4,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.GET_PROFESSORS_WITH_SAME_STUDENTS))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.assignProfessor(Color.RED);
        assertEquals(Set.of(Color.RED),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.assignProfessor(Color.GREEN);
        assertEquals(Set.of(Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.CYAN);
        gameState.assignProfessor(Color.CYAN);
        assertEquals(Set.of(Color.CYAN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        gameState.setCurrentPlayerSchoolBoardId(3);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.CYAN);
        gameState.assignProfessor(Color.RED);
        gameState.assignProfessor(Color.GREEN);
        gameState.assignProfessor(Color.CYAN);

        gameState.setCurrentPlayerSchoolBoardId(0);
        assertEquals(Set.of(Color.RED),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
        gameState.setCurrentPlayerSchoolBoardId(1);
        assertEquals(Set.of(Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
        gameState.setCurrentPlayerSchoolBoardId(2);
        assertEquals(Set.of(Color.CYAN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());


        gameState.setCurrentPlayerSchoolBoardId(3);
        gameState.playGetProfessorsWithSameStudents();
        assertEquals(Set.of(Color.RED, Color.GREEN, Color.CYAN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());


        gameState.assignProfessorsAfterEffect();
        gameState.resetCharacterPlayedThisTurn();
        assertEquals(Set.of(),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
        gameState.setCurrentPlayerSchoolBoardId(0);
        assertEquals(Set.of(Color.RED),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
        gameState.setCurrentPlayerSchoolBoardId(1);
        assertEquals(Set.of(Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
        gameState.setCurrentPlayerSchoolBoardId(2);
        assertEquals(Set.of(Color.CYAN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());
    }

    @Test
    public void playMoveMotherNatureToAnyArchipelago() throws GameStateInitializationFailureException, EmptyStudentSupplyException, InvalidArchipelagoIdException, MoveNotAvailableException, NoAvailableLockException, ArchipelagoAlreadyLockedException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.MOVE_MOTHER_NATURE_TO_ANY_ARCHIPELAGO),PlayableCharacter.createCharacter(Character.LOCK_ARCHIPELAGO))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.setCurrentPlayerSchoolBoardId(0);

        gameState.getCurrentPlayerSchoolBoard().addProfessor(Color.RED);
        for (Archipelago archipelago: gameState.archipelagos) {
            archipelago.addStudent(Color.RED);
        }
        gameState.playMoveMotherNatureToAnyArchipelago(11);
        if(gameState.getArchipelagoFromSingleIslandCode(11).isPresent())
            assertEquals(gameState.getCurrentPlayerSchoolBoard().getTowerColor(), gameState.getArchipelagoFromSingleIslandCode(11).get().getTowerColor());

        gameState.playMoveMotherNatureToAnyArchipelago(5);
        if(gameState.getArchipelagoFromSingleIslandCode(5).isPresent())
            assertEquals(gameState.getCurrentPlayerSchoolBoard().getTowerColor(), gameState.getArchipelagoFromSingleIslandCode(11).get().getTowerColor());

        gameState.playCharacterLock(7);
        if(gameState.getArchipelagoFromSingleIslandCode(7).isPresent()){
            assertTrue(gameState.getArchipelagoFromSingleIslandCode(7).get().isLocked());
            gameState.playMoveMotherNatureToAnyArchipelago(7);
            assertEquals(TowerColor.NONE, gameState.getArchipelagoFromSingleIslandCode(7).get().getTowerColor());
        }

    }

    @Test
    public void lockArchipelago() throws GameStateInitializationFailureException, EmptyStudentSupplyException, ArchipelagoAlreadyLockedException, InvalidArchipelagoIdException, NoAvailableLockException, MoveNotAvailableException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.LOCK_ARCHIPELAGO), PlayableCharacter.createCharacter(Character.MOVE_MOTHER_NATURE_TO_ANY_ARCHIPELAGO))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.setCurrentPlayerSchoolBoardId(0);

        gameState.getCurrentPlayerSchoolBoard().addProfessor(Color.RED);
        for (Archipelago archipelago: gameState.archipelagos) {
            archipelago.addStudent(Color.RED);
        }

        gameState.playCharacterLock(1);
        if(gameState.getArchipelagoFromSingleIslandCode(1).isPresent())
        gameState.getArchipelagoFromSingleIslandCode(1).get().setTowerColor(gameState.getCurrentPlayerSchoolBoard().getTowerColor());
        assertEquals(TowerColor.NONE, gameState.getArchipelagoFromSingleIslandCode(1).get().getTowerColor());

        if(gameState.getArchipelagoFromSingleIslandCode(2).isPresent())
        gameState.getArchipelagoFromSingleIslandCode(2).get().setTowerColor(gameState.getCurrentPlayerSchoolBoard().getTowerColor());
        assertFalse(gameState.getArchipelagoFromSingleIslandCode(1).get().merge(gameState.getArchipelagoFromSingleIslandCode(2).get()));

        assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().isLocked());
        gameState.playMoveMotherNatureToAnyArchipelago(1);
        gameState.getArchipelagoFromSingleIslandCode(1).get().isLocked();
        assertFalse(gameState.getArchipelagoFromSingleIslandCode(1).get().isLocked());
        gameState.playMoveMotherNatureToAnyArchipelago(1);
        assertEquals(gameState.getCurrentPlayerSchoolBoard().getTowerColor(), gameState.getArchipelagoFromSingleIslandCode(1).get().getTowerColor());
    }

    @Test
    public void playTwoAdditionalSteps() throws GameStateInitializationFailureException, EmptyStudentSupplyException, MoveNotAvailableException, InvalidNumberOfStepsException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.TWO_ADDITIONAL_STEPS)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.schoolBoardIdsToCardsPlayedThisRound.put(gameState.getCurrentPlayerSchoolBoardId(), Card.DOG);

        gameState.moveMotherNatureNStepsClockwise(1);
        gameState.playTwoAdditionalSteps();
        gameState.moveMotherNatureNStepsClockwise(1);
        gameState.moveMotherNatureNStepsClockwise(2);
        gameState.moveMotherNatureNStepsClockwise(3);

        assertThrows(InvalidNumberOfStepsException.class, () -> gameState.moveMotherNatureNStepsClockwise(4));
    }

    @Test
    public void playTowersDontCount() throws GameStateInitializationFailureException, EmptyStudentSupplyException, MoveNotAvailableException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.TOWERS_DONT_COUNT)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.setCurrentPlayerSchoolBoardId(0);

        for (Archipelago archipelago: gameState.archipelagos ) {
            archipelago.setTowerColor(gameState.getCurrentPlayerSchoolBoard().getTowerColor());
        }

        for (Color color: Color.values()) {
            gameState.getCurrentPlayerSchoolBoard().addProfessor(color);
        }

        if(gameState.getInfluence(List.of(0)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(0)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(1)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(1)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(2)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(2)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(3)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(3)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(4)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(4)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(5)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(5)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(6)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(6)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(7)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(7)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(8)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(8)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(9)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(9)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(10)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(10)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(11)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(11)).get().get(gameState.currentPlayerSchoolBoardId).intValue());

        gameState.playTowersDontCount();

        if(gameState.getInfluence(List.of(0)).isPresent())
            assertEquals(0, gameState.getInfluence(List.of(0)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(1)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(1)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(2)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(2)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(3)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(3)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(4)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(4)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(5)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(5)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(6)).isPresent())
            assertEquals(0, gameState.getInfluence(List.of(6)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(7)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(7)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(8)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(8)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(9)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(9)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(10)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(10)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(11)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(11)).get().get(gameState.currentPlayerSchoolBoardId).intValue());

        gameState.setTowersInfluenceForAllArchipelagos(true);

        if(gameState.getInfluence(List.of(0)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(0)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(1)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(1)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(2)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(2)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(3)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(3)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(4)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(4)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(5)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(5)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(6)).isPresent())
            assertEquals(1, gameState.getInfluence(List.of(6)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(7)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(7)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(8)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(8)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(9)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(9)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(10)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(10)).get().get(gameState.currentPlayerSchoolBoardId).intValue());
        if(gameState.getInfluence(List.of(11)).isPresent())
            assertEquals(2, gameState.getInfluence(List.of(11)).get().get(gameState.currentPlayerSchoolBoardId).intValue());

    }

    @Test
    public void playSwapThreeStudentsBetweenCharacterAndEntrance() throws GameStateInitializationFailureException, EmptyStudentSupplyException, StudentNotInTheEntranceException, StudentNotOnCharacterException, InvalidStudentListsLengthException, MoveNotAvailableException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.SWAP_THREE_STUDENTS_BETWEEN_CHARACTER_AND_ENTRANCE)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        List<Color> students1 = gameState.getAvailableCharacters().get(0).getStudents().subList(0,3);
        List<Color> students2 = gameState.getCurrentPlayerSchoolBoard().getStudentsInTheEntrance().subList(0,3);

        gameState.playSwapThreeStudentsBetweenCharacterAndEntrance(students1,students2);

        assertTrue(gameState.getAvailableCharacters().get(0).containsAllStudents(students2));
        assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheEntrance(students1));
    }

    @Test
    public void playTwoAdditionalInfluence() throws GameStateInitializationFailureException, EmptyStudentSupplyException, NotEnoughCoinsException, MoveNotAvailableException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.TWO_ADDITIONAL_INFLUENCE)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        for (Archipelago archipelago: gameState.archipelagos ) {
            archipelago.setTowerColor(gameState.getCurrentPlayerSchoolBoard().getTowerColor());
        }

        if(gameState.getInfluence(List.of(0)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(0)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(1)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(1)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(2)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(2)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(3)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(3)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(4)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(4)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(5)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(5)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(6)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(6)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(7)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(7)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(8)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(8)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(9)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(9)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(10)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(10)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(11)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(11)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());

        gameState.playTwoAdditionalInfluence();

        if(gameState.getInfluence(List.of(0)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(0)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(1)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(1)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(2)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(2)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(3)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(3)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(4)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(4)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(5)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(5)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(6)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(6)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(7)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(7)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(8)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(8)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(9)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(9)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(10)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(10)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(11)).isPresent())
            assertEquals(3,gameState.getInfluence(List.of(11)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());

        gameState.resetCharacterPlayedThisTurn();

        if(gameState.getInfluence(List.of(0)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(0)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(1)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(1)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(2)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(2)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(3)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(3)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(4)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(4)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(5)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(5)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(6)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(6)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(7)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(7)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(8)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(8)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(9)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(9)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(10)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(10)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        if(gameState.getInfluence(List.of(11)).isPresent())
            assertEquals(1,gameState.getInfluence(List.of(11)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());

    }

    @Test
    public void playColorDoesntCount() throws GameStateInitializationFailureException, EmptyStudentSupplyException, NotEnoughCoinsException, MoveNotAvailableException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.COLOR_DOESNT_COUNT)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        for (Color color: Color.values() ) {
            gameState.getCurrentPlayerSchoolBoard().addProfessor(color);
        }

        if(gameState.getInfluence(List.of(1)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(1)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(1).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(1).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(1)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }

        if(gameState.getInfluence(List.of(2)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(2)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(2).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(2).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(2)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }

        if(gameState.getInfluence(List.of(3)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(3)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(3).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(3).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(3)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }

        if(gameState.getInfluence(List.of(4)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(4)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(4).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(4).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(4)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }

        if(gameState.getInfluence(List.of(5)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(5)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(5).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(5).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(5)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }

        if(gameState.getInfluence(List.of(7)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(7)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(7).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(7).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(7)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }

        if(gameState.getInfluence(List.of(8)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(8)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(8).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(8).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(8)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }

        if(gameState.getInfluence(List.of(9)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(9)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(9).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(9).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(9)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }

        if(gameState.getInfluence(List.of(10)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(10)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(10).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(10).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(10)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }

        if(gameState.getInfluence(List.of(11)).isPresent()){
            assertEquals(1,gameState.getInfluence(List.of(11)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            if(gameState.getArchipelagoFromSingleIslandCode(11).isPresent())
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(11).get().getStudents().get(0));
            assertEquals(0,gameState.getInfluence(List.of(11)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            gameState.resetColorThatDoesntCountForAllArchipelagos();
        }


    }

    @Test
    public void playSwapTwoStudentsBetweenEntranceAndDiningRoom() throws GameStateInitializationFailureException, EmptyStudentSupplyException, NotEnoughCoinsException, StudentsNotInTheDiningRoomException, StudentNotInTheEntranceException, InvalidStudentListsLengthException, MoveNotAvailableException, FullDiningRoomLaneException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.SWAP_TWO_STUDENTS_BETWEEN_ENTRANCE_AND_DINING_ROOM)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        List<Color> studentsFromEntrance = gameState.getCurrentPlayerSchoolBoard().getStudentsInTheEntrance().subList(0,2);

        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.GREEN);

        List<Color> studentsFromDining = new ArrayList<>(List.of(Color.RED, Color.GREEN));

        gameState.playSwapTwoStudentsBetweenEntranceAndDiningRoom(studentsFromDining,studentsFromEntrance);


        assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheEntrance(studentsFromDining));
        assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheDiningRoom(studentsFromEntrance));
    }

    @Test
    public void playPutOneStudentFromCharacterToDiningRoom() throws GameStateInitializationFailureException, EmptyStudentSupplyException, NotEnoughCoinsException, StudentNotOnCharacterException, FullDiningRoomLaneException, MoveNotAvailableException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.PUT_ONE_STUDENT_FROM_CHARACTER_INTO_DINING_ROOM)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        Color studentToBePutInTheDiningRoom = gameState.getAvailableCharacters().get(0).getStudents().get(0);

        assertEquals(4, gameState.getAvailableCharacters().get(0).getStudents().size());
        gameState.playPutOneStudentFromCharacterToDiningRoom(studentToBePutInTheDiningRoom);

        assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheDiningRoom(List.of(studentToBePutInTheDiningRoom)));
        assertEquals(3, gameState.getAvailableCharacters().get(0).getStudents().size());

        studentToBePutInTheDiningRoom = gameState.getAvailableCharacters().get(0).getStudents().get(0);
        gameState.playPutOneStudentFromCharacterToDiningRoom(studentToBePutInTheDiningRoom);
        assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheDiningRoom(List.of(studentToBePutInTheDiningRoom)));
        assertEquals(2, gameState.getAvailableCharacters().get(0).getStudents().size());

        studentToBePutInTheDiningRoom = gameState.getAvailableCharacters().get(0).getStudents().get(0);
        gameState.playPutOneStudentFromCharacterToDiningRoom(studentToBePutInTheDiningRoom);
        assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheDiningRoom(List.of(studentToBePutInTheDiningRoom)));
        assertEquals(1, gameState.getAvailableCharacters().get(0).getStudents().size());

        studentToBePutInTheDiningRoom = gameState.getAvailableCharacters().get(0).getStudents().get(0);
        gameState.playPutOneStudentFromCharacterToDiningRoom(studentToBePutInTheDiningRoom);
        assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheDiningRoom(List.of(studentToBePutInTheDiningRoom)));
        assertEquals(0, gameState.getAvailableCharacters().get(0).getStudents().size());


    }

    @Test
    public void playPutThreeStudentsInTheBag2() throws GameStateInitializationFailureException, EmptyStudentSupplyException, FullDiningRoomLaneException, NotEnoughCoinsException, MoveNotAvailableException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.PUT_THREE_STUDENTS_IN_THE_BAG)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);

        gameState.schoolBoards.get(1).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(1).addStudentToDiningRoom(Color.RED);

        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.playPutThreeStudentsInTheBag(Color.RED);

        assertEquals(1, gameState.schoolBoards.get(0).getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
        assertEquals(0, gameState.schoolBoards.get(1).getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
    }

    @Test
    public void playPutThreeStudentsInTheBag3() throws GameStateInitializationFailureException, EmptyStudentSupplyException, FullDiningRoomLaneException, NotEnoughCoinsException, MoveNotAvailableException {
        GameState gameState = new ExpertGameState(3, List.of(PlayableCharacter.createCharacter(Character.PUT_THREE_STUDENTS_IN_THE_BAG)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);

        gameState.schoolBoards.get(1).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(1).addStudentToDiningRoom(Color.RED);

        gameState.schoolBoards.get(2).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(2).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(2).addStudentToDiningRoom(Color.RED);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.playPutThreeStudentsInTheBag(Color.RED);

        assertEquals(1, gameState.schoolBoards.get(0).getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
        assertEquals(0, gameState.schoolBoards.get(1).getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
        assertEquals(0, gameState.schoolBoards.get(2).getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
    }

    @Test
    public void coinsTest1() throws GameStateInitializationFailureException, EmptyStudentSupplyException, FullDiningRoomLaneException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.PUT_THREE_STUDENTS_IN_THE_BAG)));

        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());

        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        assertEquals(2, gameState.getCurrentPlayerSchoolBoard().getCoins());

        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.GREEN);
        assertEquals(2, gameState.getCurrentPlayerSchoolBoard().getCoins());
    }


    @Test
    public void coinsTest2() throws GameStateInitializationFailureException, EmptyStudentSupplyException, FullDiningRoomLaneException, NotEnoughCoinsException, MoveNotAvailableException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.PUT_THREE_STUDENTS_IN_THE_BAG)));

        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());

        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        assertEquals(2, gameState.getCurrentPlayerSchoolBoard().getCoins());

        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        assertEquals(2, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        assertEquals(2, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        assertEquals(3, gameState.getCurrentPlayerSchoolBoard().getCoins());

        gameState.playPutThreeStudentsInTheBag(Color.RED);
        assertEquals(0, gameState.getCurrentPlayerSchoolBoard().getCoins());

        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.GREEN);
        assertEquals(0, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.GREEN);
        assertEquals(0, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.GREEN);
        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());
    }
}
