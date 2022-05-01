package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Card;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.exceptions.*;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class GameStateTest {

    @Test
    public void fillCloud() throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        GameState g2 = new GameState(2);
        GameState g21 = new GameState(2);

        GameState g3 = new GameState(3);

        GameState g4 = new GameState(4);

        int cloudsCount, cloudCapacity;

        // 2 players
        cloudsCount = 2;
        cloudCapacity = 3;
        try {
            for(int i = 0; i < cloudsCount; i++)
                g2.fillCloud(i);

            for(int i = 0; i < cloudsCount; i++)
                assertEquals(cloudCapacity, g2.getClouds().get(i).size());
        } catch (FullCloudException | EmptyStudentSupplyException e) {
            fail();
        }

        // 2 players - should throw FullCloudException
        g21.fillCloud(0);
        assertThrows(FullCloudException.class, () -> g21.fillCloud(0));

        // 3 players
        cloudsCount = 3;
        cloudCapacity = 4;
        try {
            for(int i = 0; i < cloudsCount; i++)
                g3.fillCloud(i);

            for(int i = 0; i < cloudsCount; i++)
                assertEquals(cloudCapacity, g3.getClouds().get(i).size());
        } catch (FullCloudException | EmptyStudentSupplyException e) {
            e.printStackTrace();
            fail();
        }

        // 4 players
        cloudsCount = 4;
        cloudCapacity = 3;
        try {
            for(int i = 0; i < cloudsCount; i++)
                g4.fillCloud(i);

            for(int i = 0; i < cloudsCount; i++)
                assertEquals(cloudCapacity, g4.getClouds().get(i).size());
        } catch (FullCloudException | EmptyStudentSupplyException e) {
            fail();
        }
    }

    @Test
    public void grabStudentsFromCloud1() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());
    }

    @Test
    public void grabStudentsFromCloud2() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());
    }

    @Test
    public void grabStudentsFromCloud3() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(3);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());
    }

    @Test
    public void grabStudentsFromCloud4() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(3);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());

    }

    @Test
    public void grabStudentsFromCloud5() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(4);

        gameState.fillClouds();

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
    public void grabStudentsFromCloud6() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(4);

        gameState.fillClouds();

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
    public void grabStudentsFromCloudEmptyCloud1() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());


        assertThrows(EmptyCloudException.class, () -> gameState.grabStudentsFromCloud(0));

    }

    //THIS TEST SHOULD THROW AN EXCEPTION
    @Test
    public void grabStudentsFromCloudEmptyCloud2() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(3);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());


        assertThrows(EmptyCloudException.class, () -> gameState.grabStudentsFromCloud(0));
    }

    //THIS TEST SHOULD THROW AN EXCEPTION
    @Test
    public void grabStudentsFromCloudEmptyCloud3() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(4);

        gameState.fillClouds();

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
    public void grabStudentsFromCloudIllegalArgument() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.fillClouds();

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
                        assertEquals(card, g.getSchoolBoardIdsToCardsPlayedThisRound().get(id));
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
    public void moveStudentFromEntranceToArchipelago4() throws GameStateInitializationFailureException, StudentNotInTheEntranceException{
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
    public void moveStudentFromEntranceToArchipelago5() throws GameStateInitializationFailureException, StudentNotInTheEntranceException{
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
    public void moveStudentFromEntranceToArchipelago6() throws GameStateInitializationFailureException, StudentNotInTheEntranceException{
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
    public void moveStudentFromEntranceToArchipelago7() throws GameStateInitializationFailureException, StudentNotInTheEntranceException{
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

    //Contended professor
    @Test
    public void assignProfessor5() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(2);
        Color studentToBeMoved;

        int count = 0;

        do{
            if(count >= 7){
                gameState = new GameState(2);
                count = 0;
            }

            //Currentplayer 0
            gameState.setCurrentPlayerSchoolBoardId(0);
            //studentToBeMoved is a student that the player 0 has in his entrance
            studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(count);


            //CurrentPlayer 1
            gameState.setCurrentPlayerSchoolBoardId(1);
            //Increase the count of times this while cycle was performed. If it exceedes the number of students in the entrance, it means that the 2 players
            //don't share any students of the same color in their entrance.
            count++;
        }while (!gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(studentToBeMoved)); //Repeat the cycle if the player 1 doesn't have the studentToBeMoved in his entrance


        //schoolBoardId 1 is the current player
        gameState.setCurrentPlayerSchoolBoardId(1);
        //Player 1 shouldn't have the professor before assigning it to him
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));
        //Move the student from the entrance to the diningrom
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        //Assign the professor to the player 1. He should get it since no other player have a student in the corresponding diningRoom.
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));


        //schoolBoard 0 is now the current player
        gameState.setCurrentPlayerSchoolBoardId(0);
        //Player 0 shouldn't have the professor before assignProfessor()
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));
        //Move the student from the entrance to the diningrom
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        //AssignProfessor should remove the professor from player 1, since the professor is now contended.
        gameState.assignProfessor(studentToBeMoved);

        //Neither of the 2 players should now have the professor, because it is contended
        //SchoolBoardId 0 shouldn't have it
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));


        gameState.setCurrentPlayerSchoolBoardId(1);
        //SchoolBoardId 1 shouldn't have it
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

    }

    @Test
    public void assignProfessor6() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(3);
        Color studentToBeMoved;

        int count = 0;

        do{
            if(count >= 9){
                gameState = new GameState(3);
                count = 0;
            }

            //Currentplayer 0
            gameState.setCurrentPlayerSchoolBoardId(0);
            //studentToBeMoved is a student that the player 0 has in his entrance
            studentToBeMoved = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(count);


            //CurrentPlayer 1
            gameState.setCurrentPlayerSchoolBoardId(1);
            //Increase the count of times this while cycle was performed. If it exceedes the number of students in the entrance, it means that the 2 players
            //don't share any students of the same color in their entrance.
            count++;
        }while (!gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(studentToBeMoved)); //Repeat the cycle if the player 1 doesn't have the studentToBeMoved in his entrance


        gameState.setCurrentPlayerSchoolBoardId(2);
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        //schoolBoardId 1 is the current player
        gameState.setCurrentPlayerSchoolBoardId(1);
        //Player 1 shouldn't have the professor before assigning it to him
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));
        //Move the student from the entrance to the diningrom
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        //Assign the professor to the player 1. He should get it since no other player have a student in the corresponding diningRoom.
        gameState.assignProfessor(studentToBeMoved);
        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));


        gameState.setCurrentPlayerSchoolBoardId(2);
        gameState.assignProfessor(studentToBeMoved);
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        //schoolBoard 0 is now the current player
        gameState.setCurrentPlayerSchoolBoardId(0);
        //Player 0 shouldn't have the professor before assignProfessor()
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));
        //Move the student from the entrance to the diningrom
        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        //AssignProfessor should remove the professor from player 1, since the professor is now contended.
        gameState.assignProfessor(studentToBeMoved);

        //Neither of the 3 players should now have the professor, because it is contended
        //SchoolBoardId 0 shouldn't have it
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));


        gameState.setCurrentPlayerSchoolBoardId(1);
        //SchoolBoardId 1 shouldn't have it
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

        gameState.setCurrentPlayerSchoolBoardId(2);
        assertFalse(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

    }


    //2 PLAYERS
    @Test
    public void getInfluence1() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {

        GameState gameState = new GameState(2);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student = null;

        gameState.setCurrentPlayerSchoolBoardId(0);
        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);


        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);


        assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
    }

    @Test
    public void getInfluence2() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {

        GameState gameState = new GameState(2);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student = null;

        gameState.setCurrentPlayerSchoolBoardId(0);


        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(student);
        gameState.assignProfessor(student);
        motherNaturePosition.addStudent(student);
        motherNaturePosition.addStudent(student);

        assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));

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
            assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        }


        assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));

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
            assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        }
        else
            assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));

        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));

    }

    //3 PLAYERS
    @Test
    public void getInfluence5() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(3);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student = null;

        gameState.setCurrentPlayerSchoolBoardId(0);
        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);


        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);


        assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));

    }

    @Test
    public void getInfluence6() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {

        GameState gameState = new GameState(3);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student = null;

        gameState.setCurrentPlayerSchoolBoardId(0);


        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(student);
        gameState.assignProfessor(student);
        motherNaturePosition.addStudent(student);
        motherNaturePosition.addStudent(student);

        assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));

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
            assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        }
        else
            assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));


        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));

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
            assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        }
        else
            assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));

        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));
    }

    //4 PLAYERS
    @Test
    public void getInfluence9() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(4);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student = null;

        gameState.setCurrentPlayerSchoolBoardId(0);
        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);


        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);


        assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(3));

    }

    @Test
    public void getInfluence10() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(4);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student = null;

        gameState.setCurrentPlayerSchoolBoardId(0);


        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        gameState.moveStudentFromEntranceToDiningRoom(student);
        gameState.assignProfessor(student);
        motherNaturePosition.addStudent(student);
        motherNaturePosition.addStudent(student);

        assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(3));


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
            assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
            assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));
        }
        else
        {
            assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
            assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));
        }



        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(3));


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
            assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
            assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));
        }
        else{
            assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
            assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));
        }


        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(3));

    }

   /* @Test
    public void getInfluence13() throws GameStateInitializationFailureException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(4);
        Archipelago motherNaturePosition = gameState.getArchipelagosForTesting().get(0);
        Color student = null;

        gameState.setCurrentPlayerSchoolBoardId(0);
        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);


        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);

        assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(1,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(3));

        gameState.setCurrentPlayerSchoolBoardId(2);
        student = gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);


        gameState.moveStudentFromEntranceToDiningRoom(student);

        gameState.assignProfessor(student);

        motherNaturePosition.addStudent(student);

        assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(0));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(1));
        assertEquals(2,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(2));
        assertEquals(0,(int) gameState.getInfluence(motherNaturePosition.getIslandCodes()).get(3));
    }
*/
    @Test
    public void mergeWithPrevious1() throws GameStateInitializationFailureException {
        GameState gameState = new GameState(2);

        gameState.setMotherNaturePositionForTesting(gameState.getArchipelagosForTesting().stream().filter(archipelago -> archipelago.getIslandCodes().contains(0)).findFirst().orElse(null));
        gameState.conquerArchipelago(0);

        gameState.setMotherNaturePositionForTesting(gameState.getArchipelagosForTesting().stream().filter(archipelago -> archipelago.getIslandCodes().contains(1)).findFirst().orElse(null));
        gameState.conquerArchipelago(0);

        gameState.mergeWithPrevious();

        assertTrue(gameState.getMotherNaturePositionIslandCodes().contains(0));
        assertTrue(gameState.getMotherNaturePositionIslandCodes().contains(1));

    }

    @Test
    public void mergeWithNext1() throws GameStateInitializationFailureException {
        GameState gameState = new GameState(2);

        gameState.setMotherNaturePositionForTesting(gameState.getArchipelagosForTesting().stream().filter(archipelago -> archipelago.getIslandCodes().contains(1)).findFirst().orElse(null));
        gameState.conquerArchipelago(0);

        gameState.setMotherNaturePositionForTesting(gameState.getArchipelagosForTesting().stream().filter(archipelago -> archipelago.getIslandCodes().contains(0)).findFirst().orElse(null));
        gameState.conquerArchipelago(0);

        gameState.mergeWithNext();

        assertTrue(gameState.getMotherNaturePositionIslandCodes().contains(0));
        assertTrue(gameState.getMotherNaturePositionIslandCodes().contains(1));

    }


}

