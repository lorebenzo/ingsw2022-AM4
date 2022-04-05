package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Card;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.exceptions.*;
import org.junit.Test;

import java.util.List;
import java.util.Set;
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
                        assertEquals(card, g.getSchoolBoardIdToCardPlayedThisRound().get(id));
                    } catch (CardIsNotInTheDeckException | InvalidSchoolBoardIdException e) {
                        fail();
                    }
                }
            }
        }
    }

    @Test
    public void moveStudentFromEntranceToDiningRoom() {

    }

    @Test
    public void moveStudentFromEntranceToArchipelago() {
    }

    //2 PLAYERS
    @Test
    public void getInfluence1() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
        GameState gameState = new GameState(2);
        Set<Color> playerProfessors = Stream.of(Color.RED)
                .collect(Collectors.toSet());
        Archipelago motherNaturePosition = new Archipelago(0);

        motherNaturePosition.addStudent(Color.RED);

        gameState.setMotherNaturePosition(motherNaturePosition);

        gameState.setCurrentPlayerSchoolBoardId(0);
        gameState.setCurrentPlayerProfessor(Color.RED);

        assertEquals(1,gameState.getInfluence());


    }

    @Test
    public void getInfluence2() throws GameStateInitializationFailureException, InvalidSchoolBoardIdException {
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

        assertEquals(1,gameState.getInfluence());


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

        assertEquals(2,gameState.getInfluence());


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

        assertEquals(3,gameState.getInfluence());

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
        gameState.conquestArchipelago();

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

        assertEquals(1,gameState.getInfluence());


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

        assertEquals(1,gameState.getInfluence());


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

        assertEquals(2,gameState.getInfluence());


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

        assertEquals(3,gameState.getInfluence());

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

        assertEquals(4,gameState.getInfluence());

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

        assertEquals(1,gameState.getInfluence());


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

        assertEquals(1,gameState.getInfluence());


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

        assertEquals(2,gameState.getInfluence());


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

        assertEquals(3,gameState.getInfluence());

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

        assertEquals(4,gameState.getInfluence());

    }

}