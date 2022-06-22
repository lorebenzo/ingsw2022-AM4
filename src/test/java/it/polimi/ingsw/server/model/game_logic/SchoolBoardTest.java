package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.GameConstants;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SchoolBoardTest {

    @Test
    public void playCard() throws EmptyStudentSupplyException {
        StudentFactory s = new StudentFactory();
        SchoolBoard schoolBoard = new SchoolBoard(0, s.getNStudents(7), TowerColor.BLACK);

        int expectedDeckSize = GameConstants.MAX_NUMBER_OF_CARDS.value;

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
                        .noneMatch(i -> i >= GameConstants.DINING_ROOM_LANE_SIZE.value)
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

    @Test
    public void removeStudentFromEntrance1() {
        List<Color> entrance = Stream.of(Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.PURPLE, Color.CYAN, Color.PURPLE).collect(Collectors.toList());

        int initialEntranceSize = entrance.size();

        SchoolBoard schoolBoard = new SchoolBoard(0,entrance,TowerColor.BLACK);

        try{
            schoolBoard.removeStudentFromEntrance(Color.RED);
        }catch (StudentNotInTheEntranceException e)
        {
            fail();
        }

        assertTrue(entrance.containsAll(schoolBoard.getStudentsInTheEntrance()));
        assertEquals(initialEntranceSize -1, schoolBoard.getStudentsInTheEntrance().size());
    }

    //Should throw StudentNotInTheEntranceException because the required Student Color isn't in the entrance
    @Test
    public void removeStudentFromEntrance2() {
        List<Color> entrance = Stream.of(Color.GREEN, Color.YELLOW, Color.CYAN, Color.PURPLE, Color.CYAN, Color.PURPLE).collect(Collectors.toList());

        int initialEntranceSize = entrance.size();

        SchoolBoard schoolBoard = new SchoolBoard(0,entrance,TowerColor.BLACK);


        assertThrows(StudentNotInTheEntranceException.class, () -> schoolBoard.removeStudentFromEntrance(Color.RED));
        assertTrue(entrance.containsAll(schoolBoard.getStudentsInTheEntrance()));
        assertTrue(schoolBoard.getStudentsInTheEntrance().containsAll(entrance));
        assertEquals(initialEntranceSize, schoolBoard.getStudentsInTheEntrance().size());
    }

    @Test
    public void removeStudentFromEntrance3() {
        List<Color> entrance = Stream.of(Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.PURPLE, Color.CYAN, Color.PURPLE).collect(Collectors.toList());

        int initialEntranceSize = entrance.size();

        SchoolBoard schoolBoard = new SchoolBoard(0,entrance,TowerColor.BLACK);

        try{
            schoolBoard.removeStudentFromEntrance(Color.RED);
            schoolBoard.removeStudentFromEntrance(Color.GREEN);
            schoolBoard.removeStudentFromEntrance(Color.YELLOW);
            schoolBoard.removeStudentFromEntrance(Color.PURPLE);
        }catch (StudentNotInTheEntranceException e)
        {
            fail();
        }

        assertTrue(entrance.containsAll(schoolBoard.getStudentsInTheEntrance()));
        assertEquals(initialEntranceSize -4, schoolBoard.getStudentsInTheEntrance().size());
    }

    //Should throw an exception, since the test is trying to remove a color that is not present
    @Test
    public void removeStudentFromEntrance4() {
        List<Color> entrance = Stream.of(Color.RED).collect(Collectors.toList());

        SchoolBoard schoolBoard = new SchoolBoard(0,entrance,TowerColor.BLACK);

        assertThrows(StudentNotInTheEntranceException.class, () -> schoolBoard.removeStudentFromEntrance(Color.PURPLE));
    }

    @Test
    public void removeStudentFromEntrance5() {
        List<Color> entrance = Stream.of(Color.RED).collect(Collectors.toList());

        SchoolBoard schoolBoard = new SchoolBoard(0,entrance,TowerColor.BLACK);

        assertThrows(IllegalArgumentException.class, () ->schoolBoard.removeStudentFromEntrance(null));
    }

    @Test
    public void grabStudentsFromCloud1() {
        List<Color> entrance = Stream.of(Color.CYAN, Color.PURPLE).collect(Collectors.toList());
        List<Color> cloud = Stream.of(Color.RED, Color.GREEN, Color.YELLOW).collect(Collectors.toList());

        int initialEntranceSize = entrance.size();

        SchoolBoard schoolBoard = new SchoolBoard(0,entrance,TowerColor.BLACK);
        schoolBoard.grabStudentsFromCloud(cloud);

        assertTrue(schoolBoard.getStudentsInTheEntrance().containsAll(cloud));
        assertEquals(initialEntranceSize + cloud.size(), schoolBoard.getStudentsInTheEntrance().size());

    }

    @Test
    public void grabStudentsFromCloud2() {
        List<Color> entrance = Stream.of(Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.PURPLE, Color.CYAN, Color.PURPLE).collect(Collectors.toList());
        List<Color> cloud = Stream.of(Color.RED, Color.GREEN, Color.YELLOW).collect(Collectors.toList());

        int initialEntranceSize = entrance.size();

        SchoolBoard schoolBoard = new SchoolBoard(0,entrance,TowerColor.BLACK);
        schoolBoard.grabStudentsFromCloud(cloud);

        assertTrue(schoolBoard.getStudentsInTheEntrance().containsAll(cloud));
        assertEquals(initialEntranceSize + cloud.size(), schoolBoard.getStudentsInTheEntrance().size());

    }

    //Should throw an exception, since the cloud reference is null
    @Test
    public void grabStudentsFromCloud4() {
        List<Color> entrance = Stream.of(Color.PURPLE).collect(Collectors.toList());
        List<Color> cloud = new LinkedList<>();
        cloud.add(Color.PURPLE);
        cloud.add(null);
        cloud.add(Color.RED);

        SchoolBoard schoolBoard = new SchoolBoard(0,entrance,TowerColor.BLACK);

        assertThrows(IllegalArgumentException.class, () -> schoolBoard.grabStudentsFromCloud(cloud) );

    }

    @Test
    public void containsAllStudents() {
       SchoolBoard schoolBoard = new SchoolBoard(0,new ArrayList<>(List.of(Color.GREEN)),TowerColor.BLACK);
       assertFalse(schoolBoard.containsAllStudentsInTheEntrance(List.of(Color.GREEN, Color.GREEN)));
    }

    @Test
    public void containsAllStudents2() {
        SchoolBoard schoolBoard = new SchoolBoard(0,new ArrayList<>(List.of(Color.GREEN)),TowerColor.BLACK);
        assertTrue(schoolBoard.containsAllStudentsInTheEntrance(List.of(Color.GREEN)));
    }

    @Test
    public void containsAllStudents3() {
        SchoolBoard schoolBoard = new SchoolBoard(0,new ArrayList<>(List.of(Color.GREEN,Color.GREEN,Color.RED)),TowerColor.BLACK);

        assertTrue(schoolBoard.containsAllStudentsInTheEntrance(List.of(Color.GREEN, Color.RED)));

    }

    @Test
    public void containsAllStudents4() {
        SchoolBoard schoolBoard = new SchoolBoard(0,new ArrayList<>(List.of(Color.GREEN,Color.GREEN,Color.RED)),TowerColor.BLACK);

        assertFalse(schoolBoard.containsAllStudentsInTheEntrance(List.of(Color.RED, Color.RED)));

    }

    @Test
    public void containsAllStudents5() {
        SchoolBoard schoolBoard = new SchoolBoard(0,new ArrayList<>(List.of(Color.GREEN,Color.GREEN,Color.RED)),TowerColor.BLACK);

        assertFalse(schoolBoard.containsAllStudentsInTheEntrance(List.of(Color.CYAN, Color.RED)));

    }
}