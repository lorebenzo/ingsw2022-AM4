package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.FullDiningRoomLaneException;
import it.polimi.ingsw.server.model.game_logic.exceptions.StudentsNotInTheDiningRoomException;

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
    protected void addStudentToDiningRoom(Color student) throws FullDiningRoomLaneException {
        super.addStudentToDiningRoom(student);

        if(this.getDiningRoomLaneColorToNumberOfStudents().get(student) != 0 && this.getDiningRoomLaneColorToNumberOfStudents().get(student) % 3 == 0)
            this.coins++;
    }

    public void payCharacter(int cost){
        this.coins = this.coins - cost;
    }

    public int getCoins() {
        return coins;
    }

    public void removeStudentFromDiningRoom(Color student) {
        if(this.diningRoomLaneColorToNumberOfStudents.get(student) >= 1)
            this.diningRoomLaneColorToNumberOfStudents.put(student, this.diningRoomLaneColorToNumberOfStudents.get(student)-1);
    }
}