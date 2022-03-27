package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Card;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.game_logic.exceptions.FullDiningRoomLaneException;
import it.polimi.ingsw.server.game_logic.exceptions.StudentNotInTheEntranceException;

import java.util.*;

public class SchoolBoard {
    private final int id; // must be unique for each GameState
    private final Map<Color, Integer> diningRoomLaneColorToNumberOfStudents;
    private final TowerColor towerColor;
    private final List<Color> studentsInTheEntrance;
    private final Set<Color> professorsTable;
    private final List<Card> deck;

    public final static int maximumNumberOfCardsInTheDeck = 10;
    public final static int maximumNumberOfStudentsInDiningRoomLanes = 10;

    public SchoolBoard(int id, List<Color> studentsInTheEntrance, TowerColor towerColor) {
        this.id = id;
        this.studentsInTheEntrance = studentsInTheEntrance;
        this.towerColor = towerColor;

        this.diningRoomLaneColorToNumberOfStudents = new HashMap<>();
        this.professorsTable = new HashSet<>();
        this.deck = new LinkedList<>();

        for(Color color : Color.values())
            this.diningRoomLaneColorToNumberOfStudents.put(color, 0);

        // Push all the 10 cards into the deck
        this.deck.addAll(Arrays.asList(Card.values()).subList(0, 10));
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
     *
     * @param card the card to be played, and hence removed from the deck
     */
    public void playCard(Card card) throws CardIsNotInTheDeckException {
        if(!this.deck.contains(card)) throw new CardIsNotInTheDeckException();
        this.deck.remove(card);
    }

    public int getId() {
        return id;
    }

    /**
     *
     * @return true if a student of the given color is in the entrance of the school board, false otherwise
     */
    public boolean isInTheEntrance(Color student) {
        return this.studentsInTheEntrance.contains(student);
    }

    /**
     *
     * @param student any student in the entrance
     * @throws StudentNotInTheEntranceException if there is not a student of that color in the entrance
     * @throws FullDiningRoomLaneException if the dining room lane corresponding to that student color is already full
     */
    public void moveFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        if(this.diningRoomLaneColorToNumberOfStudents.get(student) >= SchoolBoard.maximumNumberOfStudentsInDiningRoomLanes)
            throw new FullDiningRoomLaneException();
        this.removeStudentFromEntrance(student);
        this.diningRoomLaneColorToNumberOfStudents.put(student,
                this.diningRoomLaneColorToNumberOfStudents.get(student) + 1);
    }

    /**
     *
     * @param student any student in the entrance
     * @throws StudentNotInTheEntranceException if there is not a student of that color in the entrance
     */
    public void removeStudentFromEntrance(Color student) throws StudentNotInTheEntranceException {
        if(!this.studentsInTheEntrance.contains(student)) throw new StudentNotInTheEntranceException();
        this.studentsInTheEntrance.remove(student);
    }

    /**
     * Students are put in the entrance
     * @param studentsGrabbed students grabbed from the cloud
     */
    public void grabStudentsFromCloud(List<Color> studentsGrabbed) {
        this.studentsInTheEntrance.addAll(studentsGrabbed);
    }

    public Set<Color> getProfessors() {
        return new HashSet<>(this.professorsTable);
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    // Getters

    public List<Card> getDeck() {
        return new LinkedList<>(this.deck);
    }
}
