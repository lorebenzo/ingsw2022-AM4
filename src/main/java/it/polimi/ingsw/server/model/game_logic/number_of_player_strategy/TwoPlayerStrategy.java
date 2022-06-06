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

public class TwoPlayerStrategy implements NumberOfPlayersStrategy {
    protected final int numberOfPlayers = 2;
    private final int numberOfStudentsInEachCloud = 3;
    protected final int numberOfStudentsInTheEntrance = 7;
    private final int numberOfTowers = 8;

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

        Map<Integer,Integer> schoolBoardIdsToInfluenceMap = new HashMap<>();

        for (SchoolBoard schoolBoard: schoolBoards) {
            schoolBoardIdsToInfluenceMap.put(schoolBoard.getId(), archipelago.getInfluence(schoolBoard.getProfessors(), schoolBoard.getTowerColor()));
        }

        return schoolBoardIdsToInfluenceMap;

    }

    @Override
    public int getNumberOfTowers() {
        return this.numberOfTowers;
    }
}
