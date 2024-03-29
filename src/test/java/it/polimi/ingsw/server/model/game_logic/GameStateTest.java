package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidCardPlayedException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    @Test
    public void grabStudentsFromCloud1() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());
    }

    @Test
    public void grabStudentsFromCloud2() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());
    }

    @Test
    public void grabStudentsFromCloud3() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(3);

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());
    }

    @Test
    public void grabStudentsFromCloud4() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(3);

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());

    }

    @Test
    public void grabStudentsFromCloud5() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(4);

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());

        gameState.grabStudentsFromCloud(3);
        assertTrue(gameState.getClouds().get(3).isEmpty());
    }

    @Test
    public void grabStudentsFromCloud6() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(4);

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());

        gameState.grabStudentsFromCloud(3);
        assertTrue(gameState.getClouds().get(3).isEmpty());

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());


    }

    //THIS TEST SHOULD THROW AN EXCEPTION
    @Test
    public void grabStudentsFromCloudEmptyCloud1() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());


        assertThrows(EmptyCloudException.class, () -> gameState.grabStudentsFromCloud(0));

    }

    //THIS TEST SHOULD THROW AN EXCEPTION
    @Test
    public void grabStudentsFromCloudEmptyCloud2() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(3);

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());


        assertThrows(EmptyCloudException.class, () -> gameState.grabStudentsFromCloud(0));
    }

    //THIS TEST SHOULD THROW AN EXCEPTION
    @Test
    public void grabStudentsFromCloudEmptyCloud3() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(4);

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());

        gameState.grabStudentsFromCloud(3);
        assertTrue(gameState.getClouds().get(3).isEmpty());

        assertThrows(EmptyCloudException.class, () -> gameState.grabStudentsFromCloud(0));

    }

    //THIS TEST SHOULD THROW AN EXCEPTION
    @Test
    public void grabStudentsFromCloudIllegalArgument() throws GameStateInitializationFailureException, FullCloudException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        assertThrows(IllegalArgumentException.class ,() -> gameState.grabStudentsFromCloud(2));

    }

    @Test
    public void playCard() throws GameStateInitializationFailureException {
        List<GameState> gameStates = Stream.of(new GameState(2), new GameState(3), new GameState(4)).collect(Collectors.toList());

        for(GameState g : gameStates) {
            for(int id : g.getSchoolBoardIds()) {
                g.setCurrentPlayerSchoolBoardId(id);
                for(Card card : Card.values()) {
                    try {
                        g.playCard(card);
                        assertEquals(card, g.getSchoolBoardIdsToCardPlayedThisRound().get(id));
                    } catch (CardIsNotInTheDeckException | InvalidCardPlayedException e) {
                        fail();
                    }
                }
            }
        }
    }

    //2 PLAYERS
    @Test
    public void moveStudentFromEntranceToArchipelago1() throws GameStateInitializationFailureException{
        GameState gameState = new GameState(2);
        gameState.setCurrentPlayerSchoolBoardId(0);


        List<Integer> islandCodes = Stream.of(0).collect(Collectors.toList());

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        int movedStudentsCount = 0;

        for (Color c:Color.values()) {
            try{
                if(gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(c)){
                    gameState.moveStudentFromEntranceToArchipelago(c,islandCodes);
                    movedStudentsCount++;
                }
                assertTrue(entrance.containsAll(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance()));
            }
            catch(StudentNotInTheEntranceException e){
                fail();
            }
        }
        assertEquals(entrance.size(), gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().size() + movedStudentsCount);
    }

    //3 PLAYERS
    @Test
    public void moveStudentFromEntranceToArchipelago2() throws GameStateInitializationFailureException{
        GameState gameState = new GameState(3);
        gameState.setCurrentPlayerSchoolBoardId(0);


        List<Integer> islandCodes = Stream.of(0).collect(Collectors.toList());

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        int movedStudentsCount = 0;

        for (Color c:Color.values()) {
            try{
                if(gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(c)){
                    gameState.moveStudentFromEntranceToArchipelago(c,islandCodes);
                    movedStudentsCount++;
                }
                assertTrue(entrance.containsAll(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance()));
            }
            catch(StudentNotInTheEntranceException e){
                fail();
            }
        }
        assertEquals(entrance.size(), gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().size() + movedStudentsCount);
    }

    //4 PLAYERS
    @Test
    public void moveStudentFromEntranceToArchipelago3() throws GameStateInitializationFailureException{
        GameState gameState = new GameState(4);
        gameState.setCurrentPlayerSchoolBoardId(0);


        List<Integer> islandCodes = Stream.of(0).collect(Collectors.toList());

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        int movedStudentsCount = 0;

        for (Color c:Color.values()) {
            try{
                if(gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(c)){
                    gameState.moveStudentFromEntranceToArchipelago(c,islandCodes);
                    movedStudentsCount++;
                }
                assertTrue(entrance.containsAll(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance()));
            }
            catch(StudentNotInTheEntranceException e){
                fail();
            }
        }
        assertEquals(entrance.size(), gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().size() + movedStudentsCount);
    }

    //THIS METHOD SHOULD THROW AN EXCEPTION - ISLANDCODES PROVIDED IS NOT VALID
    @Test
    public void moveStudentFromEntranceToArchipelago4() throws GameStateInitializationFailureException {
        GameState gameState = new GameState(2);
        gameState.setCurrentPlayerSchoolBoardId(0);


        List<Integer> islandCodes = Stream.of(13).collect(Collectors.toList());

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        for (Color c:Color.values()) {
            if(gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(c)){
                assertThrows(IllegalArgumentException.class, () -> gameState.moveStudentFromEntranceToArchipelago(c,islandCodes));
            }
        }
    }

    //THIS METHOD SHOULD THROW AN EXCEPTION - ISLANDCODES CONTAINS NULL ARGUMENT
    @Test
    public void moveStudentFromEntranceToArchipelago5() throws GameStateInitializationFailureException{
        GameState gameState = new GameState(2);
        gameState.setCurrentPlayerSchoolBoardId(0);

        List<Integer> islandCodes = Stream.of((Integer)null).collect(Collectors.toList());

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        for (Color c:Color.values()) {
            if(gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(c)){
                assertThrows(IllegalArgumentException.class, () -> gameState.moveStudentFromEntranceToArchipelago(c,islandCodes));
            }
        }
    }

    //THIS METHOD SHOULD THROW AN EXCEPTION - ISLANDCODES REFERENCE IS NULL
    @Test
    public void moveStudentFromEntranceToArchipelago6() throws GameStateInitializationFailureException{
        GameState gameState = new GameState(2);
        gameState.setCurrentPlayerSchoolBoardId(0);

        List<Integer> islandCodes = null;

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        for (Color c:Color.values()) {
            if(gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(c)){
                assertThrows(IllegalArgumentException.class, () -> gameState.moveStudentFromEntranceToArchipelago(c,islandCodes));
            }
        }
    }

    //THIS METHOD SHOULD THROW AN EXCEPTION - STUDENT ARGUMENT IS NULL
    @Test
    public void moveStudentFromEntranceToArchipelago7() throws GameStateInitializationFailureException{
        GameState gameState = new GameState(2);
        gameState.setCurrentPlayerSchoolBoardId(0);

        List<Integer> islandCodes = Stream.of((Integer)null).collect(Collectors.toList());

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        assertThrows(IllegalArgumentException.class, () -> gameState.moveStudentFromEntranceToArchipelago(null,islandCodes));
    }

    @Test
    public void assignProfessor0() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(2);
        Color studentToBeMoved;

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);


        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);

        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

    }

    @Test
    public void assignProfessor1() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(2);
        Color studentToBeMoved;

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(1);


        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);

        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));
    }

    @Test
    public void assignProfessor2() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(2);
        Color studentToBeMoved;

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));
    }

    @Test
    public void assignProfessor3() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(3);
        Color studentToBeMoved;

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));
    }

    @Test
    public void assignProfessor4() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(4);
        Color studentToBeMoved;

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(0);
        studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));
    }

    //2 PLAYERS
    @Test
    public void getInfluence1() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {

        GameState gameState = new GameState(2);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student;

        gameState.setCurrentPlayerSchoolBoardId(0);
        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);


        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);

        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();

        assertEquals(1,gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        assertEquals(0,gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
    }

    @Test
    public void getInfluence2() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {

        GameState gameState = new GameState(2);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student;

        gameState.setCurrentPlayerSchoolBoardId(0);


        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(student);
        gameState.assignProfessor(student);
        motherNaturePosition.addStudent(student);
        motherNaturePosition.addStudent(student);

        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();

        assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        assertEquals(0,gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());

    }

    @Test
    public void getInfluence3() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(2);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);

        gameState.setCurrentPlayerSchoolBoardId(0);

        final Color student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        Optional<Color> student2;

        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);

        gameState.setCurrentPlayerSchoolBoardId(1);

        student2 = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance()
                .stream()
                .filter(s -> !s.equals(student))
                .findFirst();


        if(student2.isPresent()){
            gameState.moveStudentFromEntranceToDiningRoom(student2.get());

            gameState.assignProfessor(student2.get());

            motherNaturePosition.addStudent(student2.get());
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
        }

        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
        assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());

    }

    @Test
    public void getInfluence4() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(2);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);

        gameState.setCurrentPlayerSchoolBoardId(0);

        final Color student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        Optional<Color> student2;

        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);

        student2 = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance()
                .stream()
                .filter(s -> !s.equals(student))
                .findFirst();


        if(student2.isPresent()){
            gameState.moveStudentFromEntranceToDiningRoom(student2.get());

            gameState.assignProfessor(student2.get());

            motherNaturePosition.addStudent(student2.get());
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        }
        else{
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        }


        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());

    }

    //3 PLAYERS
    @Test
    public void getInfluence5() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(3);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student;

        gameState.setCurrentPlayerSchoolBoardId(0);
        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);


        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);

        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();

        assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());

    }

    @Test
    public void getInfluence6() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {

        GameState gameState = new GameState(3);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student;

        gameState.setCurrentPlayerSchoolBoardId(0);


        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(student);
        gameState.assignProfessor(student);
        motherNaturePosition.addStudent(student);
        motherNaturePosition.addStudent(student);

        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();

        assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());

    }

    @Test
    public void getInfluence7() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(3);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);

        gameState.setCurrentPlayerSchoolBoardId(0);
        final Color student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);

        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);


        Optional<Color> student2 = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance()
                .stream()
                .filter(s -> !s.equals(student))
                .findFirst();

        motherNaturePosition.addStudent(student);


        if(student2.isPresent()){
            gameState.moveStudentFromEntranceToDiningRoom(student2.get());

            gameState.assignProfessor(student2.get());

            motherNaturePosition.addStudent(student2.get());
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        }
        else{
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        }



        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());

    }

    @Test
    public void getInfluence8() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(3);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);

        gameState.setCurrentPlayerSchoolBoardId(0);

        final Color student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        Optional<Color> student2;

        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);

        student2 = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance()
                .stream()
                .filter(s -> !s.equals(student))
                .findFirst();


        if(student2.isPresent()){
            gameState.moveStudentFromEntranceToDiningRoom(student2.get());

            gameState.assignProfessor(student2.get());

            motherNaturePosition.addStudent(student2.get());
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        }
        else{
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        }

        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());
    }

    //4 PLAYERS
    @Test
    public void getInfluence9() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(4);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student;

        gameState.setCurrentPlayerSchoolBoardId(0);
        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);


        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);

        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();

        assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
        assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(3).intValue());

    }

    @Test
    public void getInfluence10() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(4);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student;

        gameState.setCurrentPlayerSchoolBoardId(0);


        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(student);
        gameState.assignProfessor(student);
        motherNaturePosition.addStudent(student);
        motherNaturePosition.addStudent(student);

        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();

        assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
        assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(3).intValue());


    }

    @Test
    public void getInfluence11() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(4);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);

        gameState.setCurrentPlayerSchoolBoardId(0);
        final Color student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);

        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);


        Optional<Color> student2 = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance()
                .stream()
                .filter(s -> !s.equals(student))
                .findFirst();

        motherNaturePosition.addStudent(student);


        if(student2.isPresent()){
            gameState.moveStudentFromEntranceToDiningRoom(student2.get());

            gameState.assignProfessor(student2.get());

            motherNaturePosition.addStudent(student2.get());
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
            assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());
        }
        else
        {
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
            assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());
        }


        if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(3).intValue());


    }

    @Test
    public void getInfluence12() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(4);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);

        gameState.setCurrentPlayerSchoolBoardId(0);

        final Color student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        Optional<Color> student2;

        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);

        student2 = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance()
                .stream()
                .filter(s -> !s.equals(student))
                .findFirst();


        if(student2.isPresent()){
            gameState.moveStudentFromEntranceToDiningRoom(student2.get());

            gameState.assignProfessor(student2.get());

            motherNaturePosition.addStudent(student2.get());
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
            assertEquals(2, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());
        }
        else{
            if(gameState.getInfluence(motherNaturePosition.getIslandCodes()).isEmpty()) fail();
            assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(0).intValue());
            assertEquals(1, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(2).intValue());
        }


        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(1).intValue());
        assertEquals(0, gameState.getInfluence(motherNaturePosition.getIslandCodes()).get().get(3).intValue());

    }

    @Test
    public void mergeWithPrevious1() throws GameStateInitializationFailureException {
        GameState gameState = new GameState(2);

        if(gameState.getArchipelagoFromSingleIslandCode(0).isEmpty() || gameState.getArchipelagoFromSingleIslandCode(1).isEmpty()) fail();


        gameState.getArchipelagoFromSingleIslandCode(0).get().addStudent(Color.RED);
        gameState.setMotherNaturePositionForTesting(gameState.getArchipelagoFromSingleIslandCode(0).get());
        gameState.conquerArchipelago(0);

        gameState.getArchipelagoFromSingleIslandCode(0).get().addStudent(Color.GREEN);
        gameState.setMotherNaturePositionForTesting(gameState.getArchipelagoFromSingleIslandCode(1).get());
        gameState.conquerArchipelago(0);

        assertTrue(gameState.mergeWithPrevious());

        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getIslandCodes().contains(0));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().getIslandCodes().contains(0));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getIslandCodes().contains(1));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().getIslandCodes().contains(1));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getStudents().contains(Color.RED));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().getStudents().contains(Color.RED));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getStudents().contains(Color.GREEN));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().getStudents().contains(Color.GREEN));
    }

    @Test
    public void mergeWithNext1() throws GameStateInitializationFailureException {
        GameState gameState = new GameState(2);

        if(gameState.getArchipelagoFromSingleIslandCode(0).isEmpty() || gameState.getArchipelagoFromSingleIslandCode(1).isEmpty()) fail();


        gameState.getArchipelagoFromSingleIslandCode(1).get().addStudent(Color.RED);
        gameState.setMotherNaturePositionForTesting(gameState.getArchipelagoFromSingleIslandCode(1).get());
        gameState.conquerArchipelago(0);

        gameState.getArchipelagoFromSingleIslandCode(0).get().addStudent(Color.GREEN);
        gameState.setMotherNaturePositionForTesting(gameState.getArchipelagoFromSingleIslandCode(0).get());
        gameState.conquerArchipelago(0);

        assertTrue(gameState.mergeWithNext());

        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getIslandCodes().contains(0));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().getIslandCodes().contains(0));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getIslandCodes().contains(1));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().getIslandCodes().contains(1));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getStudents().contains(Color.RED));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().getStudents().contains(Color.RED));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(0).get().getStudents().contains(Color.GREEN));
        assertTrue(gameState.getArchipelagoFromSingleIslandCode(1).get().getStudents().contains(Color.GREEN));
    }

    @Test
    public void checkWinnersTowers2() throws GameStateInitializationFailureException {
        GameState gameState = new GameState(2);

        for (int i = 0; i < gameState.strategy.getNumberOfTowers(); i++) {
            assertFalse(gameState.checkWinners().containsValue(true));
            gameState.archipelagos.get(i).setTowerColor(gameState.getCurrentPlayerSchoolBoard().getTowerColor());
        }

        assertTrue(gameState.checkWinners().get(gameState.getCurrentPlayerSchoolBoardId()));
        gameState.setCurrentPlayerSchoolBoardId(1);
        assertFalse(gameState.checkWinners().get(gameState.getCurrentPlayerSchoolBoardId()));
    }

    @Test
    public void checkWinnersTowers3() throws GameStateInitializationFailureException {
        GameState gameState = new GameState(3);

        for (int i = 0; i < gameState.strategy.getNumberOfTowers(); i++) {
            assertFalse(gameState.checkWinners().containsValue(true));
            gameState.archipelagos.get(i).setTowerColor(gameState.getCurrentPlayerSchoolBoard().getTowerColor());
        }

        assertTrue(gameState.checkWinners().get(gameState.getCurrentPlayerSchoolBoardId()));
        gameState.setCurrentPlayerSchoolBoardId(1);
        assertFalse(gameState.checkWinners().get(gameState.getCurrentPlayerSchoolBoardId()));
        gameState.setCurrentPlayerSchoolBoardId(2);
        assertFalse(gameState.checkWinners().get(gameState.getCurrentPlayerSchoolBoardId()));
    }

    @Test
    public void checkWinnersTowers4() throws GameStateInitializationFailureException {
        GameState gameState = new GameState(4);

        for (int i = 0; i < gameState.strategy.getNumberOfTowers(); i++) {
            assertFalse(gameState.checkWinners().containsValue(true));
            gameState.archipelagos.get(i).setTowerColor(gameState.getCurrentPlayerSchoolBoard().getTowerColor());
        }

        assertTrue(gameState.checkWinners().get(gameState.getCurrentPlayerSchoolBoardId()));
        gameState.setCurrentPlayerSchoolBoardId(1);
        assertFalse(gameState.checkWinners().get(gameState.getCurrentPlayerSchoolBoardId()));
        gameState.setCurrentPlayerSchoolBoardId(2);
        assertTrue(gameState.checkWinners().get(gameState.getCurrentPlayerSchoolBoardId()));
        gameState.setCurrentPlayerSchoolBoardId(3);
        assertFalse(gameState.checkWinners().get(gameState.getCurrentPlayerSchoolBoardId()));
    }

    @Test
    public void lastCardPlayed2() throws GameStateInitializationFailureException, InvalidCardPlayedException, CardIsNotInTheDeckException {
        GameState gameState = new GameState(2);

        assertEquals(Map.of(), gameState.schoolBoardIdsToLastCardPlayed);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.playCard(Card.DOG);
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(0));

        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.playCard(Card.GOOSE);
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.playCard(Card.CAT);
        assertEquals(Card.CAT, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));

    }

    @Test
    public void lastCardPlayed3() throws GameStateInitializationFailureException, InvalidCardPlayedException, CardIsNotInTheDeckException {
        GameState gameState = new GameState(3);

        assertEquals(Map.of(), gameState.schoolBoardIdsToLastCardPlayed);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.playCard(Card.DOG);
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(0));

        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.playCard(Card.GOOSE);
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));

        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.playCard(Card.OCTOPUS);
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));
        assertEquals(Card.OCTOPUS, gameState.schoolBoardIdsToLastCardPlayed.get(2));

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.playCard(Card.CAT);
        assertEquals(Card.CAT, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));
        assertEquals(Card.OCTOPUS, gameState.schoolBoardIdsToLastCardPlayed.get(2));

        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.playCard(Card.DOG);
        assertEquals(Card.CAT, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(2));

    }

    @Test
    public void lastCardPlayed4() throws GameStateInitializationFailureException, InvalidCardPlayedException, CardIsNotInTheDeckException {
        GameState gameState = new GameState(4);

        assertEquals(Map.of(), gameState.schoolBoardIdsToLastCardPlayed);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.playCard(Card.DOG);
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(0));

        gameState.setCurrentPlayerSchoolBoardId(1);
        gameState.playCard(Card.GOOSE);
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));

        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.playCard(Card.OCTOPUS);
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));
        assertEquals(Card.OCTOPUS, gameState.schoolBoardIdsToLastCardPlayed.get(2));

        gameState.setCurrentPlayerSchoolBoardId(3);
        gameState.playCard(Card.TURTLE);
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));
        assertEquals(Card.OCTOPUS, gameState.schoolBoardIdsToLastCardPlayed.get(2));
        assertEquals(Card.TURTLE, gameState.schoolBoardIdsToLastCardPlayed.get(3));

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.playCard(Card.CAT);
        assertEquals(Card.CAT, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));
        assertEquals(Card.OCTOPUS, gameState.schoolBoardIdsToLastCardPlayed.get(2));
        assertEquals(Card.TURTLE, gameState.schoolBoardIdsToLastCardPlayed.get(3));

        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.playCard(Card.DOG);
        assertEquals(Card.CAT, gameState.schoolBoardIdsToLastCardPlayed.get(0));
        assertEquals(Card.GOOSE, gameState.schoolBoardIdsToLastCardPlayed.get(1));
        assertEquals(Card.DOG, gameState.schoolBoardIdsToLastCardPlayed.get(2));
        assertEquals(Card.TURTLE, gameState.schoolBoardIdsToLastCardPlayed.get(3));

    }
}

