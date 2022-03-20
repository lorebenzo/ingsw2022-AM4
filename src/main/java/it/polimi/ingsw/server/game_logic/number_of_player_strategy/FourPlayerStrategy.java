package it.polimi.ingsw.server.game_logic.number_of_player_strategy;

import it.polimi.ingsw.server.game_logic.Archipelago;
import it.polimi.ingsw.server.game_logic.SchoolBoard;
import it.polimi.ingsw.server.game_logic.StudentFactory;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.EmptyStudentSupplyException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FourPlayerStrategy implements NumberOfPlayersStrategy {
    private final int numberOfPlayers = 4;
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
        schoolBoards.add(
                new SchoolBoard(2, studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.WHITE)
        );
        schoolBoards.add(
                new SchoolBoard(3, studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.BLACK)
        );

        return schoolBoards;
    }

    @Override
    public int getInfluence(List<SchoolBoard> schoolBoards, Archipelago archipelago, int currentPlayerSchoolBoardId) {
        SchoolBoard currentPlayerSchoolBoard = schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0);
        SchoolBoard currentPlayerPartnerSchoolBoard = schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getTowerColor().equals(currentPlayerSchoolBoard.getTowerColor()))
                .collect(Collectors.toList())
                .get(0);

        Set<Color> totalProfessors = new HashSet<>();
        totalProfessors.addAll(currentPlayerSchoolBoard.getProfessors());
        totalProfessors.addAll(currentPlayerPartnerSchoolBoard.getProfessors());

        return archipelago.getInfluence(totalProfessors, currentPlayerSchoolBoard.getTowerColor());
    }
}
