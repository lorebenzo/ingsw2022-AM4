package it.polimi.ingsw.server.model.game_logic.number_of_player_strategy;

import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
import it.polimi.ingsw.server.model.game_logic.StudentFactory;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.EmptyStudentSupplyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreePlayerStrategy implements NumberOfPlayersStrategy {
    private static final int numberOfPlayers = 3;
    private static final int numberOfStudentsInEachCloud = 4;
    private static final int numberOfStudentsInTheEntrance = 9;
    private static final int numberOfTowers = 6;

    @Override
    public int getNumberOfStudentsInEachCloud() {
        return numberOfStudentsInEachCloud;
    }

    @Override
    public int getNumberOfStudentsInTheEntrance() {
        return numberOfStudentsInTheEntrance;
    }

    @Override
    public List<SchoolBoard> initializeSchoolBoards(StudentFactory studentFactory) throws EmptyStudentSupplyException {
        List<SchoolBoard> schoolBoards = new ArrayList<>(numberOfPlayers);
        schoolBoards.add(
                new SchoolBoard(0, studentFactory.getNStudents(numberOfStudentsInTheEntrance), TowerColor.WHITE)
        );
        schoolBoards.add(
                new SchoolBoard(1, studentFactory.getNStudents(numberOfStudentsInTheEntrance), TowerColor.BLACK)
        );
        schoolBoards.add(
                new SchoolBoard(2, studentFactory.getNStudents(numberOfStudentsInTheEntrance), TowerColor.GRAY)
        );
        return schoolBoards;
    }

/*    @Override
    public int getInfluence(List<SchoolBoard> schoolBoards, Archipelago archipelago, int currentPlayerSchoolBoardId) {
        SchoolBoard currentPlayerSchoolBoard = schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0);
        return archipelago.getInfluence(currentPlayerSchoolBoard.getProfessors(), currentPlayerSchoolBoard.getTowerColor());
    }*/

    /**
     * @return a Map<Integer, Integer> where the key is the schoolBoardId and the value is the influence on the inputed archipelago
     * @param schoolBoards is a List<SchoolBoard> containing the list of all schoolBoards
     * @param archipelago is the Archipelago on which the influence  calculated
     * */
    public Map<Integer, Integer> getInfluence(List<SchoolBoard> schoolBoards, Archipelago archipelago){
        if(schoolBoards == null || schoolBoards.contains(null) || archipelago == null)
            throw new IllegalArgumentException();

        Map<Integer,Integer> schoolBoardsToInfluenceMap = new HashMap<>();

        for (SchoolBoard schoolBoard: schoolBoards) {
            schoolBoardsToInfluenceMap.put(schoolBoard.getId(), archipelago.getInfluence(schoolBoard.getProfessors(), schoolBoard.getTowerColor()));
        }

        return schoolBoardsToInfluenceMap;

    }

    @Override
    public int getNumberOfTowers() {
        return numberOfTowers;
    }
}

