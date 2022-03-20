package it.polimi.ingsw.server.game_logic.number_of_player_strategy;

import it.polimi.ingsw.server.game_logic.Archipelago;
import it.polimi.ingsw.server.game_logic.SchoolBoard;
import it.polimi.ingsw.server.game_logic.StudentFactory;
import it.polimi.ingsw.server.game_logic.exceptions.EmptyStudentSupplyException;

import java.util.List;

public interface NumberOfPlayersStrategy {
    int getNumberOfStudentsInEachCloud();
    int getNumberOfStudentsInTheEntrance();
    List<SchoolBoard> initializeSchoolBoards(StudentFactory studentFactory) throws EmptyStudentSupplyException;
    int getInfluence(List<SchoolBoard> schoolBoards, Archipelago archipelago, int currentPlayerSchoolBoardId);
}
