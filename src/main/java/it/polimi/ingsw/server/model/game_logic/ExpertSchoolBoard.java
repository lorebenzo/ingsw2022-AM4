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

    /**
     * This method adds the inputted student to the diningRoom, adding a coin to the schoolBoard in certain conditions
     * @param student is the student that will be added to the diningRoom
     * @throws FullDiningRoomLaneException if the diningRoom table corresponding to the inputted student is already full
     */
    @Override
    protected void addStudentToDiningRoom(Color student) throws FullDiningRoomLaneException {
        super.addStudentToDiningRoom(student);

        if(this.getDiningRoomLaneColorToNumberOfStudents().get(student) != 0 && this.getDiningRoomLaneColorToNumberOfStudents().get(student) % 3 == 0)
            this.coins++;
    }

    /**
     * This method is used to pay for the characters that may be activated during a game
     * @param cost is the cost of the character that will be activated
     */
    public void payCharacter(int cost){
        this.coins = this.coins - cost;
    }

    /**
     * This method returns the amount of coins that the schoolBoard has
     * @return an int representing the amount of coins that the schoolBoard has
     */
    public int getCoins() {
        return coins;
    }

    /**
     * This method removes the inputted student from the diningRoom
     * @param student is the student that will be removed from the diningRoom
     * @throws StudentsNotInTheDiningRoomException if the inputted student is not actually present in the diningRoom
     */
    public void removeStudentFromDiningRoom(Color student) throws StudentsNotInTheDiningRoomException {
        if(this.diningRoomLaneColorToNumberOfStudents.get(student) < 1) throw new StudentsNotInTheDiningRoomException();
            this.diningRoomLaneColorToNumberOfStudents.put(student, this.diningRoomLaneColorToNumberOfStudents.get(student)-1);
    }

    /**
     * This method returns the light version of the schoolBoard, containing all the useful information that need to be sent over the network
     * @return the light version of the schoolBoard, containing all the useful information that need to be sent over the network
     */
    @Override
    public LightSchoolBoard lightify() {
        return new LightSchoolBoard(
                super.id,
                super.diningRoomLaneColorToNumberOfStudents,
                super.towerColor,
                super.studentsInTheEntrance,
                super.professorsTable,
                super.deck,

                this.coins
        );
    }
}