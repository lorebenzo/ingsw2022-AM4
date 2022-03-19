package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.*;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    private final int numberOfPlayers;
    private final int numberOfStudentsInEachCloud;
    private final int numberOfStudentsInTheEntrance;

    // Game flow attributes
    private int currentRound; // rounds start at 0, and get incremented when all players play a turn
    private int currentTurn; // the id of the school board owned by the current player
    private int currentSubTurn; // the sub-turn resets at the start of each turn and gets incremented when the current player makes a move
    public enum Phase {
        PLANNING, ACTION
    }
    private Phase currentPhase;
    private final Map<Integer, Card> schoolBoardIdToCardPlayedThisRound;

    private final List<Archipelago> archipelagos;
    private final List<SchoolBoard> schoolBoards;
    private final List<List<Color>> clouds;
    private final StudentFactory studentFactory;
    private Archipelago motherNaturePosition;

    private int currentPlayerSchoolBoardId;

    /**
     *
     * @param numberOfPlayers number of players in the game, must be between 2 (inclusive) and 4 (inclusive)
     */
    public GameState(int numberOfPlayers) throws GameStateInitializationFailureException {
        if(numberOfPlayers < 2 || numberOfPlayers > 4) throw new IllegalArgumentException();

        this.numberOfPlayers = numberOfPlayers;
        this.numberOfStudentsInEachCloud = numberOfPlayers == 3 ? 4 : 3;
        this.numberOfStudentsInTheEntrance = numberOfPlayers == 3 ? 9 : 7;

        this.currentRound = 0;
        this.currentTurn = 0;
        this.currentSubTurn = 0;
        this.schoolBoardIdToCardPlayedThisRound = new HashMap<>();

        this.studentFactory = new StudentFactory();
        try {
            this.archipelagos = this.initializeArchipelagos();
            this.schoolBoards = this.initializeSchoolBoards();
            this.clouds = this.initializeClouds();
        } catch (EmptyStudentSupplyException e) {
            e.printStackTrace();
            throw new GameStateInitializationFailureException();
        }
    }

    private List<List<Color>> initializeClouds() throws EmptyStudentSupplyException {
        List<List<Color>> clouds = new ArrayList<>(this.numberOfPlayers);

        // Create a cloud for each player
        for(int i = 0; i < this.numberOfPlayers; i++) {
            List<Color> cloud = new ArrayList<>(this.numberOfStudentsInEachCloud);
            clouds.add(cloud);
        }

        return clouds;
    }

    private List<Archipelago> initializeArchipelagos() throws EmptyStudentSupplyException {
        List<Archipelago> archipelagos = new LinkedList<>();
        for(int i = 1; i <= 12; i++) {
            Archipelago newArchipelago = new Archipelago(i);
            if(i == 1) this.motherNaturePosition = newArchipelago;
            if(i != 1 && i != 6) newArchipelago.addStudent(this.studentFactory.getStudent());
            archipelagos.add(newArchipelago);
        }
        return archipelagos;
    }

    private List<SchoolBoard> initializeSchoolBoards() throws EmptyStudentSupplyException {
        List<SchoolBoard> schoolBoards = new ArrayList<>(numberOfPlayers);
        schoolBoards.add(
                new SchoolBoard(0, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.WHITE)
        );
        schoolBoards.add(
                new SchoolBoard(1, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.BLACK)
        );
        if(this.numberOfPlayers == 3)
            schoolBoards.add(
                    new SchoolBoard(2, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.GRAY)
            );
        if(this.numberOfPlayers == 4) {
            schoolBoards.add(
                    new SchoolBoard(2, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.WHITE)
            );
            schoolBoards.add(
                    new SchoolBoard(3, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.BLACK)
            );
        }

        return schoolBoards;
    }

    // Game flow methods

    /**
     */
    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    /**
     * @requires schoolBoardId is a valid id of an existing school board in this game
     */
    public void setCurrentPlayerSchoolBoardId(int schoolBoardId) {
        this.currentPlayerSchoolBoardId = schoolBoardId;
    }

    // Planning phase methods

    /**
     * @requires (cloudIndex >= this.numberOfPlayers || cloudIndex < 0)
     *            && \* the cloud at cloudIndex doesn't contain any student *\
     * @param cloudIndex the index of the cloud to fill with students
     */
    public void fillCloud(int cloudIndex) throws FullCloudException, EmptyStudentSupplyException {
        if(cloudIndex >= this.numberOfPlayers || cloudIndex < 0) throw new IllegalArgumentException();
        List<Color> chosenCloud = this.clouds.get(cloudIndex);
        if(!chosenCloud.isEmpty()) throw new FullCloudException();
        chosenCloud.addAll(this.studentFactory.getNStudents(this.numberOfStudentsInEachCloud));
    }

    /**
     * The current player plays the given card
     * @requires the current player actually owns that card && card != null && school board ID exists
     * @param card the card to be played by the current player
     */
    public void playCard(Card card) throws CardIsNotInTheDeckException {
        if(card == null) throw new IllegalArgumentException();
        if(!this.schoolBoards.stream()
                .map(SchoolBoard::getId)
                .collect(Collectors.toSet())
                .contains(currentPlayerSchoolBoardId)) throw new IllegalArgumentException("SchoolBoardID does not exist");

        this.schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0)
                .playCard(card);
        this.schoolBoardIdToCardPlayedThisRound.put(currentPlayerSchoolBoardId, card);
    }

    /**
     * @requires student is in the current player's entrance
     */
    public void moveStudentFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        SchoolBoard currentPlayerSchoolBoard = this.schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == this.currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0);

        currentPlayerSchoolBoard.moveFromEntranceToDiningRoom(student);
    }


    // Getters

    public List<List<Color>> getClouds() {
        return new LinkedList<>(this.clouds);
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Set<Integer> getSchoolBoardIds() {
        return this.schoolBoards.stream()
                .map(SchoolBoard::getId)
                .collect(Collectors.toSet());
    }

    public Map<Integer, Card> getSchoolBoardIdToCardPlayedThisRound() {
        return new HashMap<>(this.schoolBoardIdToCardPlayedThisRound);
    }
}
