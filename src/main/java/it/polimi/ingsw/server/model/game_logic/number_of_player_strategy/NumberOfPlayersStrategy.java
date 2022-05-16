package it.polimi.ingsw.server.model.game_logic.number_of_player_strategy;

import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
import it.polimi.ingsw.server.model.game_logic.StudentFactory;
import it.polimi.ingsw.server.model.game_logic.exceptions.EmptyStudentSupplyException;

import java.util.List;
import java.util.Map;

public interface NumberOfPlayersStrategy {
    int getNumberOfStudentsInEachCloud();
    int getNumberOfStudentsInTheEntrance();
    int getNumberOfTowers();
    List<SchoolBoard> initializeSchoolBoards(StudentFactory studentFactory) throws EmptyStudentSupplyException;
    //int getInfluence(List<SchoolBoard> schoolBoards, Archipelago archipelago, int currentPlayerSchoolBoardId);
    Map<Integer, Integer> getInfluence(List<SchoolBoard> schoolBoards, Archipelago archipelago);
}
