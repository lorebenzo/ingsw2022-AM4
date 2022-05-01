package it.polimi.ingsw.server.game_logic.number_of_player_strategy;

import it.polimi.ingsw.server.game_logic.Archipelago;
import it.polimi.ingsw.server.game_logic.SchoolBoard;
import it.polimi.ingsw.server.game_logic.StudentFactory;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.EmptyStudentSupplyException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

/*    @Override
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
    }*/

    //TODO da migliorare
    /**
     * @return a Map<Integer, Integer> where the key is the schoolBoardId and the value is the influence on the inputed archipelago
     * @param schoolBoards is a List<SchoolBoard> containing the list of all schoolBoards
     * @param archipelago is the Archipelago on which the influence  calculated
     * */
    public Map<Integer, Integer> getInfluence(List<SchoolBoard> schoolBoards, Archipelago archipelago){
        if(schoolBoards == null || schoolBoards.contains(null) || archipelago == null)
            throw new IllegalArgumentException();

        Map<SchoolBoard,Integer> schoolBoardsToInfluenceMap = new HashMap<>();
        Map<Integer, Integer> teamsInfluences = new HashMap<>();

        for (SchoolBoard schoolBoard: schoolBoards) {
            schoolBoardsToInfluenceMap.put(schoolBoard, archipelago.getInfluence(schoolBoard.getProfessors(), schoolBoard.getTowerColor()));
        }

        int whiteTowerInfluence = schoolBoardsToInfluenceMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getTowerColor() == TowerColor.WHITE)
                .mapToInt(Map.Entry::getValue)
                .sum();

        int blackTowerInfluence = schoolBoardsToInfluenceMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getTowerColor() == TowerColor.BLACK)
                .mapToInt(Map.Entry::getValue)
                .sum();

        for (SchoolBoard schoolBoard: schoolBoards) {
            if(schoolBoard.getTowerColor() == TowerColor.WHITE)
                teamsInfluences.put(schoolBoard.getId(), whiteTowerInfluence);
            else if(schoolBoard.getTowerColor() == TowerColor.BLACK)
                teamsInfluences.put(schoolBoard.getId(), blackTowerInfluence);
        }

        return teamsInfluences;

    }
}
