package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.GameConstants;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.model.game_logic.exceptions.FullDiningRoomLaneException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;

import java.util.*;

public class SchoolBoard implements SchoolBoardCommonInterface{
    protected final int id; // must be unique for each GameState
    protected final Map<Color, Integer> diningRoomLaneColorToNumberOfStudents;
    protected final TowerColor towerColor;
    protected final List<Color> studentsInTheEntrance;
    protected final Set<Color> professorsTable;
    protected final List<Card> deck;

    /**
     * @param id must be a unique id for this schoolBoard in this game
     * @param studentsInTheEntrance students to put in the entrance of the schoolBoard
     * @param towerColor tower color of this schoolBoard
     * @throws IllegalArgumentException if studentsInTheEntrance == null or studentsInTheEntrance contains null
     *
     */
    public SchoolBoard(int id, List<Color> studentsInTheEntrance, TowerColor towerColor) {
        if(
                studentsInTheEntrance == null || studentsInTheEntrance.contains(null)
        ) throw new IllegalArgumentException();
        this.id = id;
        this.studentsInTheEntrance = studentsInTheEntrance;
        this.towerColor = towerColor;

        this.diningRoomLaneColorToNumberOfStudents = new HashMap<>();
        this.professorsTable = new HashSet<>();
        this.deck = new LinkedList<>();

        for(Color color : Color.values())
            this.diningRoomLaneColorToNumberOfStudents.put(color, 0);

        // Push all the 10 cards into the deck
        this.deck.addAll(List.of(Card.values()));
    }

    private SchoolBoard(int id,
                        Map<Color, Integer> diningRoomLaneColorToNumberOfStudents,
                        TowerColor towerColor,
                        List<Color> studentsInTheEntrance,
                        Set<Color> professorsTable,
                        List<Card> deck) {
        this.id = id;
        this.diningRoomLaneColorToNumberOfStudents = diningRoomLaneColorToNumberOfStudents;
        this.towerColor = towerColor;
        this.studentsInTheEntrance = studentsInTheEntrance;
        this.professorsTable = professorsTable;
        this.deck = deck;
    }

    /**
     * This method receives an argument of type Card and proceeds removing the corresponding card from the player's deck of playable cards.
     * @throws IllegalArgumentException if(card == null)
     * @throws CardIsNotInTheDeckException if the card is not contained in the deck if(!this.deck.contains(card))
     * @param card the card to be played, and hence removed from the deck
     */
    public void playCard(Card card) throws CardIsNotInTheDeckException {
        if(card == null) throw new IllegalArgumentException();
        if(!this.deck.contains(card)) throw new CardIsNotInTheDeckException();
        this.deck.remove(card);
    }

    /**
     * This method returns the ID associated with the schoolBoard
     * @return an int representing the ID of the schoolBoard
     */
    public int getId() {
        return id;
    }

    /**
     * @throws IllegalArgumentException if(student == null)
     * @return true if at least a student of the given color is in the entrance of the school board, false otherwise
     */
    public boolean isInTheEntrance(Color student) {
        if(student == null) throw new IllegalArgumentException();
        return this.studentsInTheEntrance.contains(student);
    }

    /**
     * @throws IllegalArgumentException if(student == null)
     * @throws StudentNotInTheEntranceException if the student is not in the entrance
     * @throws FullDiningRoomLaneException if the diningRoomLane is full
     */
    public void moveFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        if(student == null) throw new IllegalArgumentException();
        this.removeStudentFromEntrance(student);

        this.addStudentToDiningRoom(student);
    }

    /**
     * This method adds the inputted student to the diningRoom
     * @param student is the student that will be added to the diningRoom
     * @throws FullDiningRoomLaneException if the diningRoom table corresponding to the inputted student is already full
     */
    protected void addStudentToDiningRoom(Color student) throws FullDiningRoomLaneException {
        if(this.diningRoomLaneColorToNumberOfStudents.get(student) >= GameConstants.DINING_ROOM_LANE_SIZE.value)
            throw new FullDiningRoomLaneException();
        this.diningRoomLaneColorToNumberOfStudents.put(student, this.diningRoomLaneColorToNumberOfStudents.get(student) + 1);
    }

    /**
     * @throws IllegalArgumentException if(student == null)
     * @throws StudentNotInTheEntranceException if the student is not contained in the list representing the students in the entrance
     */
    public void removeStudentFromEntrance(Color student) throws StudentNotInTheEntranceException {
        if(student == null) throw new IllegalArgumentException();
        if(!this.studentsInTheEntrance.contains(student)) throw new StudentNotInTheEntranceException();
        this.studentsInTheEntrance.remove(student);
    }

    /**
     * This method adds the inputted student to the entrance
     * @param student is the student that will be added to the entrance
     */
    public void addStudentToEntrance(Color student){
        if(student == null) throw new IllegalArgumentException();
        this.studentsInTheEntrance.add(student);
    }

    /**
     * This method checks if the inputted list of students is a subList of the students in the entrance
     * @param students is the list of students that have to be checked
     * @return true if all the elements of the inputted list are contained in the entrance (counting duplicates), false otherwise
     */
    public boolean containsAllStudentsInTheEntrance(List<Color> students){
        boolean allStudentsArePresent = true;

        List<Color> entranceCopy = new ArrayList<>(this.studentsInTheEntrance);

        for (Color student: students) {
            if(!entranceCopy.remove(student)) allStudentsArePresent = false;
        }
        return allStudentsArePresent;
    }

    /**
     * This method checks if the inputted list of students is a subList of the students in the diningRoom
     * @param students is the list of students that have to be checked
     * @return true if all the elements of the inputted list are contained in the diningRoom (counting duplicates), false otherwise
     */
    public boolean containsAllStudentsInTheDiningRoom(List<Color> students){
        boolean allStudentsArePresent = true;
        Map<Color, Integer> diningRoomCopy = new HashMap<>(this.diningRoomLaneColorToNumberOfStudents);

        for (Color student: students) {
            if(diningRoomCopy.get(student) <= 0) allStudentsArePresent = false;
            else
                diningRoomCopy.put(student, diningRoomCopy.get(student) -1);
        }
        return allStudentsArePresent;

    }

    /**
     * Students are put in the entrance
     * @throws IllegalArgumentException if(studentsGrabbed == null or studentsGrabbed contains null)
     * @param studentsGrabbed students grabbed from the cloud
     */
    public void grabStudentsFromCloud(List<Color> studentsGrabbed) {
        if(studentsGrabbed == null || studentsGrabbed.contains(null)) throw new IllegalArgumentException();
        this.studentsInTheEntrance.addAll(studentsGrabbed);
    }

    /**
     * This method returns the set of professors that are controlled by the schoolBoard
     * @return a set containing all the professors controlled by the schoolBoard
     */
    public Set<Color> getProfessors() {
        return new HashSet<>(this.professorsTable);
    }

    /**
     * This method returns the tower color of the schoolBoard
     * @return the TowerColor of the schoolBoard
     */
    public TowerColor getTowerColor() {
        return towerColor;
    }

    /**
     * Returns the deck made up by the remaining assistant cards yet to be played
     * @return a list made up by the remaining assistant cards yet to be played
     */
    public List<Card> getDeck() {
        return new LinkedList<>(this.deck);
    }

    /**
     * Returns the list of students that are in the entrance of the schoolBoard
     * @return the list of students that are in the entrance of the schoolBoard
     */
    public List<Color> getStudentsInTheEntrance() {
        return new LinkedList<>(this.studentsInTheEntrance);
    }

    /**
     * This method adds a professor to the schoolBoard
     * @param professor is the color representing the professor that has to be added to the schoolBoard
     */
    public void addProfessor(Color professor){
        if(professor == null)
            throw new IllegalArgumentException();
        this.professorsTable.add(professor);
    }

    /**
     * This method removes a professor from the schoolBoard
     * @param professor is the color representing the professor that has to be removed
     */
    public void removeProfessor(Color professor) {
        if(professor == null)
            throw new IllegalArgumentException();
        this.professorsTable.remove(professor);
    }

    /**
     * This method returns a map that links every diningRoom lane to the number of students that are in it
     * @return a map that links every diningRoom lane to the number of students that are in it
     */
    public Map<Color, Integer> getDiningRoomLaneColorToNumberOfStudents() {
        return new HashMap<>(diningRoomLaneColorToNumberOfStudents);
    }

    /**
     * This method returns the light version of the schoolBoard, containing all the useful information that need to be sent over the network
     * @return the light version of the schoolBoard, containing all the useful information that need to be sent over the network
     */
    public LightSchoolBoard lightify() {
        return new LightSchoolBoard(
                this.id,
                this.diningRoomLaneColorToNumberOfStudents,
                this.towerColor,
                this.studentsInTheEntrance,
                this.professorsTable,
                this.deck,
                null);
    }
}
