package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Card;
import it.polimi.ingsw.server.game_logic.exceptions.*;
import org.junit.Test;

import java.util.List;
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
}