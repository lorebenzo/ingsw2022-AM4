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
    public void fillCloud() throws GameStateInitializationFailureException {
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
        try {
            g21.fillCloud(0);
            g21.fillCloud(0);

            fail();
        } catch (FullCloudException | EmptyStudentSupplyException e) {
            e.printStackTrace();
        }

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
    public void grabStudentsFromCloud1() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());
    }

    @Test
    public void grabStudentsFromCloud2() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(1);
        assertTrue(gameState.getClouds().get(1).isEmpty());

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());
    }

    @Test
    public void grabStudentsFromCloud3() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
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
    public void grabStudentsFromCloud4() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
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
    public void grabStudentsFromCloud5() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
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
    public void grabStudentsFromCloud6() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
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
    public void grabStudentsFromCloudEmptyCloud1() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        try {
            gameState.grabStudentsFromCloud(0);
            fail();
        }catch (EmptyCloudException e){
            e.printStackTrace();
        }
    }

    //THIS TEST SHOULD THROW AN EXCEPTION
    @Test
    public void grabStudentsFromCloudEmptyCloud2() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
        GameState gameState = new GameState(3);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());

        try {
            gameState.grabStudentsFromCloud(0);
            fail();
        }catch (EmptyCloudException e){
            e.printStackTrace();
        }

    }

    //THIS TEST SHOULD THROW AN EXCEPTION
    @Test
    public void grabStudentsFromCloudEmptyCloud3() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
        GameState gameState = new GameState(4);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        gameState.grabStudentsFromCloud(2);
        assertTrue(gameState.getClouds().get(2).isEmpty());

        gameState.grabStudentsFromCloud(3);
        assertTrue(gameState.getClouds().get(3).isEmpty());

        try {
            gameState.grabStudentsFromCloud(0);
            fail();
        }catch (EmptyCloudException e){
            e.printStackTrace();
        }

    }

    //THIS TEST SHOULD THROW AN EXCEPTION
    @Test
    public void grabStudentsFromCloudIllegalArgument() throws GameStateInitializationFailureException, FullCloudException, EmptyStudentSupplyException, InvalidSchoolBoardIdException, EmptyCloudException {
        GameState gameState = new GameState(2);

        gameState.fillClouds();

        gameState.grabStudentsFromCloud(0);
        assertTrue(gameState.getClouds().get(0).isEmpty());

        try {
            gameState.grabStudentsFromCloud(2);
            fail();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }


    }

    @Test
    public void playCard() throws GameStateInitializationFailureException {
        List<GameState> gameStates = Stream.of(
                new GameState(2), new GameState(3), new GameState(4)
        ).collect(Collectors.toList());

        for(GameState g : gameStates) {
            for(int id : g.getSchoolBoardIds()) {
                g.setCurrentPlayerSchoolBoardId(id);
                for(Card card : Card.values()) {
                    try {
                        g.playCard(card);
                        assertEquals(card, g.getSchoolBoardIdsToCardsPlayedThisRound().get(id));
                    } catch (CardIsNotInTheDeckException | InvalidSchoolBoardIdException e) {
                        fail();
                    }
                }
            }
        }
    }

    //2 PLAYERS
    @Test
    public void moveStudentFromEntranceToArchipelago1() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException{
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
    public void moveStudentFromEntranceToArchipelago2() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException{
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
    public void moveStudentFromEntranceToArchipelago3() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException{
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
    public void moveStudentFromEntranceToArchipelago4() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException, StudentNotInTheEntranceException{
        GameState gameState = new GameState(2);
        gameState.setCurrentPlayerSchoolBoardId(0);


        List<Integer> islandCodes = Stream.of(13).collect(Collectors.toList());

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        for (Color c:Color.values()) {
            try{
                if(gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(c)){
                    gameState.moveStudentFromEntranceToArchipelago(c,islandCodes);
                    fail();
                }
            }
            catch(IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }

    //THIS METHOD SHOULD THROW AN EXCEPTION - ISLANDCODES CONTAINS NULL ARGUMENT
    @Test
    public void moveStudentFromEntranceToArchipelago5() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException, StudentNotInTheEntranceException{
        GameState gameState = new GameState(2);
        gameState.setCurrentPlayerSchoolBoardId(0);

        List<Integer> islandCodes = Stream.of((Integer)null).collect(Collectors.toList());

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        for (Color c:Color.values()) {
            try{
                if(gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(c)){
                    gameState.moveStudentFromEntranceToArchipelago(c,islandCodes);
                    fail();
                }
            }
            catch(IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }

    //THIS METHOD SHOULD THROW AN EXCEPTION - ISLANDCODES REFERENCE IS NULL
    @Test
    public void moveStudentFromEntranceToArchipelago6() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException, StudentNotInTheEntranceException{
        GameState gameState = new GameState(2);
        gameState.setCurrentPlayerSchoolBoardId(0);

        List<Integer> islandCodes = null;

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        for (Color c:Color.values()) {
            try{
                if(gameState.getCurrentPlayerSchoolBoardForTesting().isInTheEntrance(c)){
                    gameState.moveStudentFromEntranceToArchipelago(c,islandCodes);
                    fail();
                }
            }
            catch(IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }

    //THIS METHOD SHOULD THROW AN EXCEPTION - STUDENT ARGUMENT IS NULL
    @Test
    public void moveStudentFromEntranceToArchipelago7() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException, StudentNotInTheEntranceException{
        GameState gameState = new GameState(2);
        gameState.setCurrentPlayerSchoolBoardId(0);

        List<Integer> islandCodes = Stream.of((Integer)null).collect(Collectors.toList());

        List<Color> entrance = new LinkedList<>(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());

        try{
            gameState.moveStudentFromEntranceToArchipelago(null,islandCodes);
            fail();
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    @Test
    public void assignProfessor1() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException, StudentNotInTheEntranceException, FullDiningRoomLaneException {
        GameState gameState = new GameState(2);

        gameState.setCurrentPlayerSchoolBoardId(0);
        Color studentToBeMoved = null;

        for (Color c: Color.values()) {
           if(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().contains(c)){
               studentToBeMoved = c;
           }

        }

        gameState.moveStudentFromEntranceToDiningRoom(studentToBeMoved);
        gameState.assignProfessor(studentToBeMoved);

        assertTrue(gameState.getCurrentPlayerSchoolBoardForTesting().getProfessors().contains(studentToBeMoved));

    }

    //2 PLAYERS
    /*
    @Test
    public void getInfluence1() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(2);
        Set<Color> playerProfessors = Stream.of(Color.RED).collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);

        //gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);

        gameState.assignProfessor(Color.RED);

        Map<Integer,Integer> expectedInfluences = new HashMap<>();

        expectedInfluences.put(gameState.getCurrentPlayerSchoolBoardId(),1);
        expectedInfluences.put(1,0);
        assertEquals(expectedInfluences,gameState.getInfluence(motherNaturePosition.getIslandCodes()));


    }
/*

    @Test
    public void getInfluence2() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(2);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.GREEN);
        motherNaturePosition.addStudent(Color.PURPLE);

        //gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.assignProfessor(Color.RED);

        assertEquals(1,gameState.getInfluence(motherNaturePosition.getIslandCodes()));


    }

    @Test
    public void getInfluence3() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(2);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.GREEN);
        motherNaturePosition.addStudent(Color.PURPLE);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);
        gameState.setCurrentPlayerProfessor(Color.PURPLE);

        assertEquals(2,gameState.getInfluenceOfMotherNaturePosition());


    }

    @Test
    public void getInfluence4() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(2);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.PURPLE);
        motherNaturePosition.addStudent(Color.YELLOW);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);
        gameState.setCurrentPlayerProfessor(Color.PURPLE);

        assertEquals(3,gameState.getInfluenceOfMotherNaturePosition());

    }

    @Test
    public void getInfluence5() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(2);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.PURPLE);
        motherNaturePosition.addStudent(Color.YELLOW);


        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);
        gameState.setCurrentPlayerProfessor(Color.PURPLE);
        gameState.conquestArchipelago(gameState.getCurrentPlayerSchoolBoardId());

        assertEquals(4,gameState.getInfluence());

    }


    //3 PLAYERS
    @Test
    public void getInfluence6() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(3);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);

        assertEquals(1,gameState.getInfluenceOfMotherNaturePosition());


    }

    @Test
    public void getInfluence7() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(3);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.GREEN);
        motherNaturePosition.addStudent(Color.PURPLE);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);

        assertEquals(1,gameState.getInfluenceOfMotherNaturePosition());


    }

    @Test
    public void getInfluence8() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(3);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.GREEN);
        motherNaturePosition.addStudent(Color.PURPLE);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);
        gameState.setCurrentPlayerProfessor(Color.PURPLE);

        assertEquals(2,gameState.getInfluenceOfMotherNaturePosition());


    }

    @Test
    public void getInfluence9() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(3);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.PURPLE);
        motherNaturePosition.addStudent(Color.YELLOW);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);
        gameState.setCurrentPlayerProfessor(Color.PURPLE);

        assertEquals(3,gameState.getInfluenceOfMotherNaturePosition());

    }

    @Test
    public void getInfluence10() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(3);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.PURPLE);
        motherNaturePosition.addStudent(Color.YELLOW);


        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);
        gameState.setCurrentPlayerProfessor(Color.PURPLE);
        gameState.conquestArchipelago();

        assertEquals(4,gameState.getInfluenceOfMotherNaturePosition());

    }

    //4 PLAYERS
    @Test
    public void getInfluence11() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(4);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);

        assertEquals(1,gameState.getInfluenceOfMotherNaturePosition());


    }

    @Test
    public void getInfluence12() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(4);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.GREEN);
        motherNaturePosition.addStudent(Color.PURPLE);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);

        assertEquals(1,gameState.getInfluenceOfMotherNaturePosition());


    }

    @Test
    public void getInfluence13() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(4);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.GREEN);
        motherNaturePosition.addStudent(Color.PURPLE);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);
        gameState.setCurrentPlayerProfessor(Color.PURPLE);

        assertEquals(2,gameState.getInfluenceOfMotherNaturePosition());


    }

    @Test
    public void getInfluence14() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(4);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.PURPLE);
        motherNaturePosition.addStudent(Color.YELLOW);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);
        gameState.setCurrentPlayerProfessor(Color.PURPLE);

        assertEquals(3,gameState.getInfluenceOfMotherNaturePosition());

    }

    @Test
    public void getInfluence15() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(4);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.RED);
        motherNaturePosition.addStudent(Color.PURPLE);
        motherNaturePosition.addStudent(Color.YELLOW);


        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);
        gameState.setCurrentPlayerProfessor(Color.PURPLE);
        gameState.conquestArchipelago();

        assertEquals(4,gameState.getInfluenceOfMotherNaturePosition());

    }*/
}
