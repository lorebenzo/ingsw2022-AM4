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
    public void grabStudentsFromCloud() {
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

}