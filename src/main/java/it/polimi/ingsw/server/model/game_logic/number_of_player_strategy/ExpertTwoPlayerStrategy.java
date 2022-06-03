package it.polimi.ingsw.server.model.game_logic.number_of_player_strategy;

import it.polimi.ingsw.server.model.game_logic.ExpertSchoolBoard;
import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
import it.polimi.ingsw.server.model.game_logic.StudentFactory;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.EmptyStudentSupplyException;

import java.util.ArrayList;
import java.util.List;

public class ExpertTwoPlayerStrategy extends TwoPlayerStrategy{

    @Override
    public List<SchoolBoard> initializeSchoolBoards(StudentFactory studentFactory) throws EmptyStudentSupplyException {
        List<SchoolBoard> schoolBoards = new ArrayList<>(numberOfPlayers);

        schoolBoards.add(
                new ExpertSchoolBoard(0, studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.WHITE,1)
        );
        schoolBoards.add(
                new ExpertSchoolBoard(1, studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.BLACK,1)
        );
        return schoolBoards;
    }
}
