package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.FullDiningRoomLaneException;

import java.util.List;

public class ExpertSchoolBoard extends SchoolBoard {

    int coins;

    /**
     * @param id                    must be a unique id for this schoolboard in this game
     * @param studentsInTheEntrance students to put in the entrance of the schoolboard
     * @param towerColor            tower color of this schoolboard
     * @throws IllegalArgumentException if studentsInTheEntrance == null or studentsInTheEntrance contains null
     */
    public ExpertSchoolBoard(int id, List<Color> studentsInTheEntrance, TowerColor towerColor, int initialCoins) {
        super(id, studentsInTheEntrance, towerColor);

        this.coins = 1;

    }

    @Override
    public void moveFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        super.moveFromEntranceToDiningRoom(student);

        if(this.getDiningRoomLaneColorToNumberOfStudents().get(student) != 0 && this.getDiningRoomLaneColorToNumberOfStudents().get(student) % 3 == 0)
            this.coins++;

    }

    public int getCoins() {
        return coins;
    }

    public void payCharacter(int cost){
        this.coins = this.coins - cost;
    }
}