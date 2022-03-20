package it.polimi.ingsw.server.game_logic.number_of_player_strategy;

import it.polimi.ingsw.server.game_logic.Archipelago;
import it.polimi.ingsw.server.game_logic.SchoolBoard;
import it.polimi.ingsw.server.game_logic.StudentFactory;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.EmptyStudentSupplyException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TwoPlayerStrategy implements NumberOfPlayersStrategy {
    private final int numberOfPlayers = 2;
    private final int numberOfStudentsInEachCloud = 3;
    private final int numberOfStudentsInTheEntrance = 7;

    @Override
    public int getNumberOfStudentsInEachCloud() {
        return this.numberOfStudentsInEachCloud;
    }

    @Override
    public int getNumberOfStudentsInTheEntrance() {
        return this.numberOfStudentsInTheEntrance;
    }

    @Override
    public List<SchoolBoard> initializeSchoolBoards(StudentFactory studentFactory) throws EmptyStudentSupplyException {
        List<SchoolBoard> schoolBoards = new ArrayList<>(numberOfPlayers);
        schoolBoards.add(
                new SchoolBoard(0, studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.WHITE)
        );
        schoolBoards.add(
                new SchoolBoard(1, studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.BLACK)
        );
        return schoolBoards;
    }

    @Override
    public int getInfluence(List<SchoolBoard> schoolBoards, Archipelago archipelago, int currentPlayerSchoolBoardId) {
        SchoolBoard currentPlayerSchoolBoard = schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0);
        return archipelago.getInfluence(currentPlayerSchoolBoard.getProfessors(), currentPlayerSchoolBoard.getTowerColor());
    }
}
