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

    //#1
    @Test
    public void playPutOneStudentFromCharacterToArchipelago() throws GameStateInitializationFailureException, EmptyStudentSupplyException, InvalidArchipelagoIdException, StudentNotOnCharacterException, MoveNotAvailableException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.PUT_ONE_STUDENT_FROM_CHARACTER_TO_ARCHIPELAGO))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }



        //Remove all students that are already present on all the archipelagos
        for (Archipelago archipelago: gameState.archipelagos ) {
            for (Color color: Color.values()) {
                archipelago.removeStudent(color);
            }
        }

        //Verify that the character has 4 students at the start of the turn
        assertEquals(4,gameState.getAvailableCharacters().get(0).getStudents().size());


        //Move a student from the character to the selected archipelago and check if the move was correctly performed
        Color studentToMove = gameState.getAvailableCharacters().get(0).getStudents().get(0);

        gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove,0);
        if(gameState.getArchipelagoFromSingleIslandCode(0).isPresent()){
            assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getStudents().contains(studentToMove));
        }
        else fail();



        //Verify that the character has 3 students now
        assertEquals(3,gameState.getAvailableCharacters().get(0).getStudents().size());
        //Move another student from the same card
        studentToMove = gameState.getAvailableCharacters().get(0).getStudents().get(0);

        gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove,1);
        if(gameState.getArchipelagoFromSingleIslandCode(1).isPresent()){
            assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().getStudents().contains(studentToMove));
        }
        else fail();


        //Verify that the character has 4 students at the start of the turn
        assertEquals(2,gameState.getAvailableCharacters().get(0).getStudents().size());
        studentToMove = gameState.getAvailableCharacters().get(0).getStudents().get(0);

        gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove,2);
        if(gameState.getArchipelagoFromSingleIslandCode(2).isPresent()){
            assertTrue(gameState.getArchipelagoFromSingleIslandCode(2).get().getStudents().contains(studentToMove));
        }
        else fail();


        //Verify that the character has 4 students at the start of the turn
        assertEquals(1,gameState.getAvailableCharacters().get(0).getStudents().size());
        studentToMove = gameState.getAvailableCharacters().get(0).getStudents().get(0);

        gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove,3);
        if(gameState.getArchipelagoFromSingleIslandCode(3).isPresent()){
            assertTrue(gameState.getArchipelagoFromSingleIslandCode(3).get().getStudents().contains(studentToMove));
        }
        else fail();

        Color studentToMove2 = studentToMove;

        assertTrue(gameState.getAvailableCharacters().get(0).getStudents().isEmpty());
        assertThrows(StudentNotOnCharacterException.class, () -> gameState.playPutOneStudentFromCharacterToArchipelago(studentToMove2,0));
    }

    //#2
    @Test
    public void playGetProfessorsWithSameStudents2() throws GameStateInitializationFailureException, EmptyStudentSupplyException, MoveNotAvailableException, FullDiningRoomLaneException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.GET_PROFESSORS_WITH_SAME_STUDENTS))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        //Prepare the schoolBoard0 to make it hold the red and green professors
        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.assignProfessor(Color.RED);
        gameState.assignProfessor(Color.GREEN);
        assertEquals(Set.of(Color.RED, Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        //Prepare the schoolBoard1 to make it try to get the professors from schoolBoard0 - this should be unsuccessful with the normal rules
        //SchoolBoard1 should only have the yellow professor
        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.YELLOW);
        gameState.assignProfessor(Color.RED);
        gameState.assignProfessor(Color.GREEN);
        gameState.assignProfessor(Color.YELLOW);
        assertEquals(Set.of(Color.YELLOW),gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors());
        assertEquals(Set.of(Color.RED, Color.GREEN),gameState.getSchoolBoardFromSchoolBoardId(0).getProfessors());


        //Playing the character the schoolBoard1 should get both green and red professors without adding any new students to its diningRoom
        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.playGetProfessorsWithSameStudents();
        assertEquals(Set.of(Color.RED, Color.GREEN, Color.YELLOW),gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors());
        assertEquals(Set.of(),gameState.getSchoolBoardFromSchoolBoardId(0).getProfessors());

        //After the effect is not valid anymore the situation should return to the initial state
        gameState.assignProfessorsAfterEffect();
        gameState.resetCharacterPlayedThisTurn();
        assertEquals(Set.of(Color.RED, Color.GREEN),gameState.getSchoolBoardFromSchoolBoardId(0).getProfessors());
        assertEquals(Set.of(Color.YELLOW),gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors());
    }

    @Test
    public void playGetProfessorsWithSameStudents3() throws GameStateInitializationFailureException, EmptyStudentSupplyException, MoveNotAvailableException, FullDiningRoomLaneException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(3,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.GET_PROFESSORS_WITH_SAME_STUDENTS))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        //Prepare schoolBoard0 to have the red professor
        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.assignProfessor(Color.RED);
        assertEquals(Set.of(Color.RED),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        //Prepare schoolBoard1 to have the green professor
        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.assignProfessor(Color.GREEN);
        assertEquals(Set.of(Color.GREEN),gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors());


        //Prepare schoolBoard2 to have the yellow professor and trying to get the red and green professors
        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.YELLOW);
        gameState.assignProfessor(Color.RED);
        gameState.assignProfessor(Color.GREEN);
        gameState.assignProfessor(Color.YELLOW);

        //Every schoolBoard should have their own single professor
        assertEquals(Set.of(Color.RED),gameState.getSchoolBoardFromSchoolBoardId(0).getProfessors());
        assertEquals(Set.of(Color.GREEN),gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors());
        assertEquals(Set.of(Color.YELLOW),gameState.getSchoolBoardFromSchoolBoardId(2).getProfessors());


        //After playing the character the schoolBoard2 should get also the red and green professors
        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.playGetProfessorsWithSameStudents();
        assertEquals(Set.of(),gameState.getSchoolBoardFromSchoolBoardId(0).getProfessors());
        assertEquals(Set.of(),gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors());
        assertEquals(Set.of(Color.RED, Color.GREEN, Color.YELLOW),gameState.getSchoolBoardFromSchoolBoardId(2).getProfessors());

        gameState.assignProfessorsAfterEffect();
        gameState.resetCharacterPlayedThisTurn();

        //After the turn the situation should return to its original state
        assertEquals(Set.of(Color.RED),gameState.getSchoolBoardFromSchoolBoardId(0).getProfessors());
        assertEquals(Set.of(Color.GREEN),gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors());
        assertEquals(Set.of(Color.YELLOW),gameState.getSchoolBoardFromSchoolBoardId(2).getProfessors());
    }

    @Test
    public void playGetProfessorsWithSameStudents4() throws MoveNotAvailableException, FullDiningRoomLaneException, GameStateInitializationFailureException, EmptyStudentSupplyException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(4,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.GET_PROFESSORS_WITH_SAME_STUDENTS))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        //Prepare schoolBoard0 to get the red professor
        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.assignProfessor(Color.RED);
        assertEquals(Set.of(Color.RED),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        //Prepare schoolBoard1 to get the green professor
        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.assignProfessor(Color.GREEN);
        assertEquals(Set.of(Color.GREEN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());

        //Prepare schoolBoard2 to get the cyan professor
        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.CYAN);
        gameState.assignProfessor(Color.CYAN);
        assertEquals(Set.of(Color.CYAN),gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors());


        //Prepare schoolBoard3 to have the same color of students in the diningRoom as every other player and to have the yellow professor
        gameState.setCurrentPlayerSchoolBoardId(3);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.GREEN);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.CYAN);
        gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToDiningRoom(Color.YELLOW);
        gameState.assignProfessor(Color.RED);
        gameState.assignProfessor(Color.GREEN);
        gameState.assignProfessor(Color.CYAN);
        gameState.assignProfessor(Color.YELLOW);


        assertEquals(Set.of(Color.RED),gameState.getSchoolBoardFromSchoolBoardId(0).getProfessors());
        assertEquals(Set.of(Color.GREEN),gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors());
        assertEquals(Set.of(Color.CYAN),gameState.getSchoolBoardFromSchoolBoardId(2).getProfessors());
        assertEquals(Set.of(Color.YELLOW),gameState.getSchoolBoardFromSchoolBoardId(3).getProfessors());

        //After playing the character the schoolBoard3 should get all professors
        gameState.setCurrentPlayerSchoolBoardId(3);
        gameState.playGetProfessorsWithSameStudents();
        assertTrue(gameState.getSchoolBoardFromSchoolBoardId(0).getProfessors().isEmpty());
        assertTrue(gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors().isEmpty());
        assertTrue(gameState.getSchoolBoardFromSchoolBoardId(2).getProfessors().isEmpty());
        assertEquals(Set.of(Color.RED, Color.GREEN, Color.CYAN, Color.YELLOW),gameState.getSchoolBoardFromSchoolBoardId(3).getProfessors());


        //After the turn finishes, the situation should return to its original state
        gameState.assignProfessorsAfterEffect();
        gameState.resetCharacterPlayedThisTurn();
        assertEquals(Set.of(Color.RED),gameState.getSchoolBoardFromSchoolBoardId(0).getProfessors());
        assertEquals(Set.of(Color.GREEN),gameState.getSchoolBoardFromSchoolBoardId(1).getProfessors());
        assertEquals(Set.of(Color.CYAN),gameState.getSchoolBoardFromSchoolBoardId(2).getProfessors());
        assertEquals(Set.of(Color.YELLOW),gameState.getSchoolBoardFromSchoolBoardId(3).getProfessors());
    }

    //#3
    @Test
    public void playMoveMotherNatureToAnyArchipelago() throws GameStateInitializationFailureException, EmptyStudentSupplyException, InvalidArchipelagoIdException, MoveNotAvailableException, NoAvailableLockException, ArchipelagoAlreadyLockedException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.MOVE_MOTHER_NATURE_TO_ANY_ARCHIPELAGO),PlayableCharacter.createCharacter(Character.LOCK_ARCHIPELAGO))));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }


        //Doing this will make sure that the player would conquer any archipelago motherNature moves to
        gameState.getSchoolBoardFromSchoolBoardId(0).addProfessor(Color.RED);
        for (Archipelago archipelago: gameState.archipelagos) {
            archipelago.addStudent(Color.RED);
        }

        //Move motherNature to archipelago 1 and test if it was conquered
        gameState.playMoveMotherNatureToAnyArchipelago(1);
        if(gameState.getArchipelagoFromSingleIslandCode(1).isPresent())
            assertEquals(gameState.getCurrentPlayerSchoolBoard().getTowerColor(), gameState.getArchipelagoFromSingleIslandCode(1).get().getTowerColor());
        else fail();

        //Move motherNature to archipelago2 and test if it was conquered and merged with archipelago1
        gameState.playMoveMotherNatureToAnyArchipelago(2);
        if(gameState.getArchipelagoFromSingleIslandCode(2).isPresent())
            assertEquals(gameState.getCurrentPlayerSchoolBoard().getTowerColor(), gameState.getArchipelagoFromSingleIslandCode(2).get().getTowerColor());
        else fail();
        assertEquals(List.of(1,2), gameState.getArchipelagoFromSingleIslandCode(2).get().getIslandCodes());


        //Test that the move properly works with the lock
        gameState.playCharacterLock(3);
        if(gameState.getArchipelagoFromSingleIslandCode(3).isPresent()){
            assertTrue(gameState.getArchipelagoFromSingleIslandCode(3).get().isLocked());
            gameState.playMoveMotherNatureToAnyArchipelago(3);
            assertEquals(TowerColor.NONE, gameState.getArchipelagoFromSingleIslandCode(3).get().getTowerColor());
            assertEquals(List.of(3), gameState.getArchipelagoFromSingleIslandCode(3).get().getIslandCodes());
            assertFalse(gameState.getArchipelagoFromSingleIslandCode(3).get().isLocked());
        }
        else fail();

    }

    //#4
    @Test
    public void lockArchipelago() throws GameStateInitializationFailureException, EmptyStudentSupplyException, ArchipelagoAlreadyLockedException, InvalidArchipelagoIdException, NoAvailableLockException, MoveNotAvailableException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2,new ArrayList<>(List.of(PlayableCharacter.createCharacter(Character.LOCK_ARCHIPELAGO), PlayableCharacter.createCharacter(Character.MOVE_MOTHER_NATURE_TO_ANY_ARCHIPELAGO))));

        TowerColor currentPlayerTowerColor = gameState.getCurrentPlayerSchoolBoard().getTowerColor();

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        //Doing this will make sure that the player would conquer any archipelago motherNature moves to
        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.getSchoolBoardFromSchoolBoardId(0).addProfessor(Color.RED);
        for (Archipelago archipelago: gameState.archipelagos) {
            archipelago.addStudent(Color.RED);
        }

        assertFalse(gameState.playMoveMotherNatureToAnyArchipelago(2));
        if(gameState.getArchipelagoFromSingleIslandCode(2).isPresent()){
            assertEquals(currentPlayerTowerColor, gameState.getArchipelagoFromSingleIslandCode(2).get().getTowerColor());
        }
        else fail();


        if(gameState.getArchipelagoFromSingleIslandCode(1).isPresent()){
            //Check that the archipelago 1 is not locked
            assertFalse(gameState.getArchipelagoFromSingleIslandCode(1).get().isLocked());
            //Lock archipelago 1
            gameState.playCharacterLock(1);
            //Check that archipelago 1 is now locked
            assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().isLocked());
            //Move motherNature to archipelago 1 to unlock it
            assertFalse(gameState.playMoveMotherNatureToAnyArchipelago(1));
            //Check that archipelago 1 is now unlocked
            assertFalse(gameState.getArchipelagoFromSingleIslandCode(1).get().isLocked());
            //Check that archipelago 1 has no tower
            assertEquals(TowerColor.NONE, gameState.getArchipelagoFromSingleIslandCode(1).get().getTowerColor());
        }
        else fail();

        assertTrue(gameState.playMoveMotherNatureToAnyArchipelago(1));
        assertEquals(currentPlayerTowerColor, gameState.getArchipelagoFromSingleIslandCode(1).get().getTowerColor());
        assertEquals(gameState.getArchipelagoFromSingleIslandCode(1).get(), gameState.getArchipelagoFromSingleIslandCode(2).get());
    }

    //#5
    @Test
    public void playTwoAdditionalSteps() throws GameStateInitializationFailureException, EmptyStudentSupplyException, MoveNotAvailableException, InvalidNumberOfStepsException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.TWO_ADDITIONAL_STEPS)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        //Play card that normally allows the player to move motherNature for 1 step
        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.schoolBoardIdsToCardPlayedThisRound.put(gameState.getCurrentPlayerSchoolBoardId(), Card.DOG);

        //Check that it's possible to move motherNature normally
        gameState.moveMotherNatureNStepsClockwise(1);
        assertThrows(InvalidNumberOfStepsException.class, () -> gameState.moveMotherNatureNStepsClockwise(2));
        //Play the character
        gameState.playTwoAdditionalSteps();
        //Check that it's possible to move motherNature 2 steps further
        gameState.moveMotherNatureNStepsClockwise(1);
        gameState.moveMotherNatureNStepsClockwise(2);
        gameState.moveMotherNatureNStepsClockwise(3);
        assertThrows(InvalidNumberOfStepsException.class, () -> gameState.moveMotherNatureNStepsClockwise(4));

        //Check that the steps logic returns to its original state after resetting the character
        gameState.resetCharacterPlayedThisTurn();
        gameState.moveMotherNatureNStepsClockwise(1);
        assertThrows(InvalidNumberOfStepsException.class, () -> gameState.moveMotherNatureNStepsClockwise(2));
    }

    //#6
    @Test
    public void playTowersDontCount() throws GameStateInitializationFailureException, EmptyStudentSupplyException, MoveNotAvailableException, NotEnoughCoinsException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.TOWERS_DONT_COUNT)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        gameState.setCurrentPlayerSchoolBoardId(0);

        //Add a tower to all the archipelagos
        for (Archipelago archipelago: gameState.archipelagos) {
            archipelago.setTowerColor(gameState.getCurrentPlayerSchoolBoard().getTowerColor());
        }

        for (int i = 0; i < 12; i++) {
            if(gameState.getInfluence(List.of(i)).isPresent())
                assertEquals(1,gameState.getInfluence(List.of(i)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        }

        //Play the character
        gameState.playTowersDontCount();

        //Check that the influences are modified by the character played
        for (int i = 0; i < 12; i++) {
            if(gameState.getInfluence(List.of(i)).isPresent())
                assertEquals(0,gameState.getInfluence(List.of(i)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        }

        //Reset the effect of the character played
        gameState.setTowersInfluenceForAllArchipelagos(true);

        //Check that all influences returned to their normal values
        for (int i = 0; i < 12; i++) {
            if(gameState.getInfluence(List.of(i)).isPresent())
                assertEquals(1,gameState.getInfluence(List.of(i)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        }
    }

    //#7
    @Test
    public void playSwapThreeStudentsBetweenCharacterAndEntrance() throws GameStateInitializationFailureException, EmptyStudentSupplyException, StudentNotInTheEntranceException, StudentNotOnCharacterException, InvalidStudentListsLengthException, MoveNotAvailableException, NotEnoughCoinsException {
        for (int i = 0; i < 100; i++) {
            GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.SWAP_THREE_STUDENTS_BETWEEN_CHARACTER_AND_ENTRANCE)));

            //Trick to have many coins to perform any kind of test
            for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
                schoolBoard.payCharacter(-100);
            }

            //Prepare the list of students to get from the character
            List<Color> studentsFromCharacter = gameState.getAvailableCharacters().get(0).getStudents().subList(0,3);
            //Prepare the list of students to get from the entrance
            List<Color> studentsFromEntrance = gameState.getCurrentPlayerSchoolBoard().getStudentsInTheEntrance().subList(0,3);

            //Play the character
            gameState.playSwapThreeStudentsBetweenCharacterAndEntrance(studentsFromCharacter,studentsFromEntrance);

            //Verify that the swap was correctly performed
            assertTrue(gameState.getAvailableCharacters().get(0).containsAllStudents(studentsFromEntrance));
            assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheEntrance(studentsFromCharacter));

            assertEquals(gameState.strategy.getNumberOfStudentsInTheEntrance(),gameState.getCurrentPlayerSchoolBoard().getStudentsInTheEntrance().size());
            assertEquals(6,gameState.getAvailableCharacters().get(0).getStudents().size());
        }

    }

    @Test
    public void playSwapThreeStudentsBetweenCharacterAndEntrance2() throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.SWAP_THREE_STUDENTS_BETWEEN_CHARACTER_AND_ENTRANCE)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        //Prepare the list of students to get from the character
        List<Color> studentsFromCharacter = gameState.getAvailableCharacters().get(0).getStudents().subList(0,2);
        //Prepare the list of students to get from the entrance
        List<Color> studentsFromEntrance = gameState.getCurrentPlayerSchoolBoard().getStudentsInTheEntrance().subList(0,3);

        //Play the character
        assertThrows(InvalidStudentListsLengthException.class, () -> gameState.playSwapThreeStudentsBetweenCharacterAndEntrance(studentsFromCharacter,studentsFromEntrance));
    }

    //#8
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

        //Check that all influences on all archipelagos are right
        for (int i = 0; i < 12; i++) {
            if(gameState.getInfluence(List.of(i)).isPresent())
                assertEquals(1,gameState.getInfluence(List.of(i)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        }

        //Play the character
        gameState.playTwoAdditionalInfluence();

        //Check that all the influences are modified accordingly
        for (int i = 0; i < 12; i++) {
            if(gameState.getInfluence(List.of(i)).isPresent())
                assertEquals(3,gameState.getInfluence(List.of(i)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        }

        //Reset the character played
        gameState.resetCharacterPlayedThisTurn();

        //Check that all the influences returned back to their original state
        for (int i = 0; i < 12; i++) {
            if(gameState.getInfluence(List.of(i)).isPresent())
                assertEquals(1,gameState.getInfluence(List.of(i)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
        }

    }

    //#9
    @Test
    public void playColorDoesntCount() throws GameStateInitializationFailureException, EmptyStudentSupplyException, NotEnoughCoinsException, MoveNotAvailableException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.COLOR_DOESNT_COUNT)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-1000);
        }

        //Add all the professors to the schoolBoard in order to be sure to have influence 1 on all archipelagos
        for (Color color: Color.values() ) {
            gameState.getCurrentPlayerSchoolBoard().addProfessor(color);
        }

        //add students to the archipelagos that are initially empyty
        gameState.archipelagos.get(0).addStudent(Color.RED);
        gameState.archipelagos.get(6).addStudent(Color.RED);

        for (int i = 0; i < 12; i++) {
            if(gameState.getInfluence(List.of(i)).isPresent() && gameState.getArchipelagoFromSingleIslandCode(i).isPresent()){
                //Check that the influence is initially 1
                assertEquals(1,gameState.getInfluence(List.of(i)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
                //Play the character
                gameState.playColorDoesntCount(gameState.getArchipelagoFromSingleIslandCode(i).get().getStudents().get(0));
                //Check that the influence is modified according to the character
                assertEquals(0,gameState.getInfluence(List.of(i)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
                //Reset the character
                gameState.resetColorThatDoesntCountForAllArchipelagos();
                //Verify that the influence returned back to its original value
                assertEquals(1,gameState.getInfluence(List.of(i)).get().get(gameState.getCurrentPlayerSchoolBoardId()).intValue());
            }  else fail();
        }

    }

    //#10
    @Test
    public void playSwapTwoStudentsBetweenEntranceAndDiningRoom() throws GameStateInitializationFailureException, EmptyStudentSupplyException, NotEnoughCoinsException, StudentsNotInTheDiningRoomException, StudentNotInTheEntranceException, InvalidStudentListsLengthException, MoveNotAvailableException, FullDiningRoomLaneException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.SWAP_TWO_STUDENTS_BETWEEN_ENTRANCE_AND_DINING_ROOM)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        //Prepare the students to get from the entrance
        List<Color> studentsFromEntrance = gameState.getCurrentPlayerSchoolBoard().getStudentsInTheEntrance().subList(0,2);

        //Add some students to the diningRoom
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.RED);
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.GREEN);

        //Prepare the students to get from the diningRoom
        List<Color> studentsFromDining = new ArrayList<>(List.of(Color.RED, Color.GREEN));

        //Play the character
        gameState.playSwapTwoStudentsBetweenEntranceAndDiningRoom(studentsFromDining,studentsFromEntrance);


        //Check if the students were correctly swapped
        assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheEntrance(studentsFromDining));
        assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheDiningRoom(studentsFromEntrance));
        assertEquals(gameState.strategy.getNumberOfStudentsInTheEntrance(),gameState.getCurrentPlayerSchoolBoard().getStudentsInTheEntrance().size());
        assertEquals(3, gameState.getCurrentPlayerSchoolBoard().getDiningRoomLaneColorToNumberOfStudents().values().stream().reduce(0,Integer::sum).intValue());

    }

    //#11
    @Test
    public void playPutOneStudentFromCharacterToDiningRoom() throws GameStateInitializationFailureException, EmptyStudentSupplyException, NotEnoughCoinsException, StudentNotOnCharacterException, FullDiningRoomLaneException, MoveNotAvailableException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.PUT_ONE_STUDENT_FROM_CHARACTER_INTO_DINING_ROOM)));
        List<Color> studentToBePutInTheDiningRoom = new ArrayList<>();

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        //Check that the diningRoom is initially empty
        assertEquals(0, gameState.getCurrentPlayerSchoolBoard().getDiningRoomLaneColorToNumberOfStudents().values().stream().reduce(0,Integer::sum).intValue());

        for (int i = 4; i >= 1 ; i--) {
            //Check that the card initially contains the correct number of students
            assertEquals(i, gameState.getAvailableCharacters().get(0).getStudents().size());
            //Prepare the student to be put in the diningRoom
            studentToBePutInTheDiningRoom.add(gameState.getAvailableCharacters().get(0).getStudents().get(0));
            //Play the character
            gameState.playPutOneStudentFromCharacterToDiningRoom(studentToBePutInTheDiningRoom.get(4-i));
            //Check that the diningRoom contains the student taken from the character
            assertTrue(gameState.getCurrentPlayerSchoolBoard().containsAllStudentsInTheDiningRoom(studentToBePutInTheDiningRoom));
        }
    }

    //#12
    @Test
    public void playPutThreeStudentsInTheBag2() throws GameStateInitializationFailureException, EmptyStudentSupplyException, FullDiningRoomLaneException, NotEnoughCoinsException, MoveNotAvailableException, StudentsNotInTheDiningRoomException {
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
    public void playPutThreeStudentsInTheBag3() throws GameStateInitializationFailureException, EmptyStudentSupplyException, FullDiningRoomLaneException, NotEnoughCoinsException, MoveNotAvailableException, StudentsNotInTheDiningRoomException {
        GameState gameState = new ExpertGameState(3, List.of(PlayableCharacter.createCharacter(Character.PUT_THREE_STUDENTS_IN_THE_BAG)));

        //Trick to have many coins to perform any kind of test
        for (SchoolBoard schoolBoard: gameState.schoolBoards ) {
            schoolBoard.payCharacter(-100);
        }

        int initialNumberOfRedStudentsInTheBag = gameState.studentFactory.studentSupply.get(Color.RED);

        //Prepare schoolBoard 0
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(0).addStudentToDiningRoom(Color.CYAN);

        //Prepare schoolBoard 1
        gameState.schoolBoards.get(1).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(1).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(1).addStudentToDiningRoom(Color.YELLOW);

        //Prepare schoolBoard 2
        gameState.schoolBoards.get(2).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(2).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(2).addStudentToDiningRoom(Color.RED);
        gameState.schoolBoards.get(2).addStudentToDiningRoom(Color.GREEN);


        //Play the character
        gameState.playPutThreeStudentsInTheBag(Color.RED);

        //Verify that the number of students in the diningRoom is correct for each color and each player
        assertEquals(1, gameState.schoolBoards.get(0).getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
        assertEquals(0, gameState.schoolBoards.get(0).getDiningRoomLaneColorToNumberOfStudents().get(Color.GREEN).intValue());
        assertEquals(0, gameState.schoolBoards.get(0).getDiningRoomLaneColorToNumberOfStudents().get(Color.YELLOW).intValue());
        assertEquals(1, gameState.schoolBoards.get(0).getDiningRoomLaneColorToNumberOfStudents().get(Color.CYAN).intValue());
        assertEquals(0, gameState.schoolBoards.get(0).getDiningRoomLaneColorToNumberOfStudents().get(Color.PURPLE).intValue());

        assertEquals(0, gameState.schoolBoards.get(1).getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
        assertEquals(0, gameState.schoolBoards.get(1).getDiningRoomLaneColorToNumberOfStudents().get(Color.GREEN).intValue());
        assertEquals(1, gameState.schoolBoards.get(1).getDiningRoomLaneColorToNumberOfStudents().get(Color.YELLOW).intValue());
        assertEquals(0, gameState.schoolBoards.get(1).getDiningRoomLaneColorToNumberOfStudents().get(Color.CYAN).intValue());
        assertEquals(0, gameState.schoolBoards.get(1).getDiningRoomLaneColorToNumberOfStudents().get(Color.PURPLE).intValue());

        assertEquals(0, gameState.schoolBoards.get(2).getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
        assertEquals(1, gameState.schoolBoards.get(2).getDiningRoomLaneColorToNumberOfStudents().get(Color.GREEN).intValue());
        assertEquals(0, gameState.schoolBoards.get(2).getDiningRoomLaneColorToNumberOfStudents().get(Color.YELLOW).intValue());
        assertEquals(0, gameState.schoolBoards.get(2).getDiningRoomLaneColorToNumberOfStudents().get(Color.CYAN).intValue());
        assertEquals(0, gameState.schoolBoards.get(2).getDiningRoomLaneColorToNumberOfStudents().get(Color.PURPLE).intValue());


        //Verify that the students taken from the diningRooms of the different players were put in the bag
        assertEquals(8,gameState.studentFactory.studentSupply.get(Color.RED) - initialNumberOfRedStudentsInTheBag);


    }

    //Coin logic tests
    @Test
    public void coinsTest1() throws GameStateInitializationFailureException, EmptyStudentSupplyException, FullDiningRoomLaneException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.PUT_THREE_STUDENTS_IN_THE_BAG)));

        //Check that the initial number of coins is 1
        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());

        //Check that the number of coins is increased every 3 students put in the same diningRoomLane
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
    public void coinsTest2() throws GameStateInitializationFailureException, EmptyStudentSupplyException, FullDiningRoomLaneException, NotEnoughCoinsException, MoveNotAvailableException, StudentsNotInTheDiningRoomException {
        GameState gameState = new ExpertGameState(2, List.of(PlayableCharacter.createCharacter(Character.PUT_THREE_STUDENTS_IN_THE_BAG)));

        //Check that the initial number of coins is 1
        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());

        //Check that the number of coins is increased every 3 students put in the same diningRoomLane
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

        //Play character
        gameState.playPutThreeStudentsInTheBag(Color.RED);
        //Check that the coins are spent to pay for the character
        assertEquals(0, gameState.getCurrentPlayerSchoolBoard().getCoins());

        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.GREEN);
        assertEquals(0, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.GREEN);
        assertEquals(0, gameState.getCurrentPlayerSchoolBoard().getCoins());
        gameState.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(Color.GREEN);
        assertEquals(1, gameState.getCurrentPlayerSchoolBoard().getCoins());
    }
}
