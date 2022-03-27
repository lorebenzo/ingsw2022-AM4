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
     * This method receives an argument of type Card and procedes removing the corresponding card from the player's deck of playable cards.
     * @throws IllegalArgumentException if(card == null)
     * @throws CardIsNotInTheDeckException if the card is not contained in the deck if(!this.deck.contains(card))
     * @param card the card to be played, and hence removed from the deck
     */
    public void playCard(Card card) throws CardIsNotInTheDeckException {
        if(card == null) throw new IllegalArgumentException();
        if(!this.deck.contains(card)) throw new CardIsNotInTheDeckException();
        this.deck.remove(card);
    }

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
        this.diningRoomLaneColorToNumberOfStudents.put(student,
                this.diningRoomLaneColorToNumberOfStudents.get(student) + 1);
    }

    /**
     * @throws IllegalArgumentException if(student == null)
     * @throws StudentNotInTheEntranceException if the student is not contained in the list representing the students in the entrance
     * @throws FullDiningRoomLaneException if the corresponding diningRoomLane is full
     */
    public void removeStudentFromEntrance(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        if(student == null) throw new IllegalArgumentException();
        if(!this.studentsInTheEntrance.contains(student)) throw new StudentNotInTheEntranceException();
        if(this.diningRoomLaneColorToNumberOfStudents.get(student) >= SchoolBoard.maximumNumberOfStudentsInDiningRoomLanes)
            throw new FullDiningRoomLaneException();
        this.studentsInTheEntrance.remove(student);
    }

    /**
     * Students are put in the entrance
     * @throws IllegalArgumentException if(studentsGrabbed == null)
     * @param studentsGrabbed students grabbed from the cloud
     */
    public void grabStudentsFromCloud(List<Color> studentsGrabbed) {
        if(studentsGrabbed == null) throw new IllegalArgumentException();
        this.studentsInTheEntrance.addAll(studentsGrabbed);
    }

    public Set<Color> getProfessors() {
        return new HashSet<>(this.professorsTable);
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    // Getters
    public Set<Color> getProfessors() {
        return new HashSet<>(this.professorsTable);
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    public List<Card> getDeck() {
        return new LinkedList<>(this.deck);
    }
}
