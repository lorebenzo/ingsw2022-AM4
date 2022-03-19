package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.game_logic.exceptions.EmptyStudentSupplyException;
import it.polimi.ingsw.server.game_logic.exceptions.FullDiningRoomLaneException;
import it.polimi.ingsw.server.game_logic.exceptions.StudentNotInTheEntranceException;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SchoolBoardTest {

    @Test
    public void playCard() throws EmptyStudentSupplyException {
        StudentFactory s = new StudentFactory();
        SchoolBoard schoolBoard = new SchoolBoard(0, s.getNStudents(7), TowerColor.BLACK);

        int expectedDeckSize = SchoolBoard.maximumNumberOfCardsInTheDeck;

        List<Card> deck = schoolBoard.getDeck();
        assertEquals(expectedDeckSize, deck.size());

        for(Card card : Card.values()) {
            try {
                schoolBoard.playCard(card);
                expectedDeckSize--;
                assertEquals(expectedDeckSize, schoolBoard.getDeck().size());
            } catch (CardIsNotInTheDeckException e) {
                fail();
            }
        }
    }

    @Test
    public void moveFromEntranceToDiningRoom() {
        StudentFactory s = new StudentFactory();
        List<Color> entrance = new LinkedList<>();
        for(int i = 0; i < 1000; i++) {
            entrance.add(s.generateStudent());
        }

        SchoolBoard schoolBoard = new SchoolBoard(
                0,
                entrance,
                TowerColor.BLACK);

        Map<Color, Integer> diningRoom = new HashMap<>();
        for(Color color : Color.values()) diningRoom.put(color, 0);


        int expectedEntranceSize = entrance.size();
        while(
                entrance.size() > 0 &&
                diningRoom.keySet().stream()
                        .map(diningRoom::get)
                        .noneMatch(i -> i >= SchoolBoard.maximumNumberOfStudentsInDiningRoomLanes)
        ) {
            assertEquals(expectedEntranceSize, entrance.size());
            try {
                Color student = entrance.get(0);
                schoolBoard.moveFromEntranceToDiningRoom(student);
                expectedEntranceSize--;
                diningRoom.put(student, diningRoom.get(student) + 1);
            } catch (StudentNotInTheEntranceException | FullDiningRoomLaneException e) {
                fail();
            }
        }
    }
}