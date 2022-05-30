package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.GameConstants;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ExpertSchoolBoardTest {

    @Test
    public void coinsTest() throws FullDiningRoomLaneException {
        List<Color> entrance = new ArrayList<>(List.of(Color.RED));

        SchoolBoard schoolBoard = new ExpertSchoolBoard(0, entrance, TowerColor.BLACK,1);

        assertEquals(1,schoolBoard.getCoins());
        schoolBoard.addStudentToDiningRoom(Color.RED);
        assertEquals(1,schoolBoard.getCoins());
        schoolBoard.addStudentToDiningRoom(Color.RED);
        assertEquals(1,schoolBoard.getCoins());
        schoolBoard.addStudentToDiningRoom(Color.RED);
        assertEquals(2,schoolBoard.getCoins());

    }

    @Test
    public void removeStudentFromEntrance() throws StudentNotInTheEntranceException {
        List<Color> entrance = new ArrayList<>(List.of(Color.RED, Color.RED));

        SchoolBoard schoolBoard = new ExpertSchoolBoard(0, entrance, TowerColor.BLACK,1);
        assertEquals(List.of(Color.RED, Color.RED), entrance);
        schoolBoard.removeStudentFromEntrance(Color.RED);
        assertEquals(List.of(Color.RED), entrance);
        schoolBoard.removeStudentFromEntrance(Color.RED);
        assertEquals(List.of(), entrance);

        assertThrows(StudentNotInTheEntranceException.class, () -> schoolBoard.removeStudentFromEntrance(Color.RED));
    }

    @Test
    public void removeStudentFromDiningRoom() throws FullDiningRoomLaneException {
        List<Color> entrance = new ArrayList<>(List.of(Color.RED, Color.RED));

        SchoolBoard schoolBoard = new ExpertSchoolBoard(0, entrance, TowerColor.BLACK,1);
        schoolBoard.addStudentToDiningRoom(Color.RED);
        schoolBoard.addStudentToDiningRoom(Color.RED);

        assertEquals(2, schoolBoard.getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
        schoolBoard.removeStudentFromDiningRoom(Color.RED);
        assertEquals(1, schoolBoard.getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());
        schoolBoard.removeStudentFromDiningRoom(Color.RED);
        assertEquals(0, schoolBoard.getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());

        schoolBoard.removeStudentFromDiningRoom(Color.RED);
        assertEquals(0, schoolBoard.getDiningRoomLaneColorToNumberOfStudents().get(Color.RED).intValue());

    }
}
