package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Card;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.*;
import it.polimi.ingsw.server.game_logic.number_of_player_strategy.NumberOfPlayersStrategy;
import it.polimi.ingsw.server.game_logic.number_of_player_strategy.NumberOfPlayersStrategyFactory;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    private final int numberOfPlayers;
    private final NumberOfPlayersStrategy strategy;
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
     * @throws IllegalArgumentException if the argument representing the number of players is not between 2 and 4
     * @throws GameStateInitializationFailureException if there is a problem in the initialization of the archipelagos
     *                                                                                             or the schoolBoards,
     *                                                                                             or the clouds.
     * @param numberOfPlayers number of players in the game, must be between 2 (inclusive) and 4 (inclusive)
     */
    public GameState(int numberOfPlayers) throws GameStateInitializationFailureException {
        if(numberOfPlayers < 2 || numberOfPlayers > 4) throw new IllegalArgumentException();

        this.numberOfPlayers = numberOfPlayers;
        this.strategy = NumberOfPlayersStrategyFactory.getCorrectStrategy(numberOfPlayers);

        this.numberOfStudentsInEachCloud = this.strategy.getNumberOfStudentsInEachCloud();
        this.numberOfStudentsInTheEntrance = this.strategy.getNumberOfStudentsInTheEntrance();

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

    /**
     * This method initializes the List<List<Color>> representing the clouds
     */
    private List<List<Color>> initializeClouds() {
        List<List<Color>> clouds = new ArrayList<>(this.numberOfPlayers);

        // Create a cloud for each player
        for(int i = 0; i < this.numberOfPlayers; i++) {
            List<Color> cloud = new ArrayList<>(this.numberOfStudentsInEachCloud);
            clouds.add(cloud);
        }

        return clouds;
    }

    /**
     * This method initializes all the archipelagos adding motherNature and the students as the rulebook commands
     * @throws EmptyStudentSupplyException if the studentSupply representing the bag is empty
     * @return a List<Archipelago> containing all the already initialized and ready to use archipelagos of the game
     */
    private List<Archipelago> initializeArchipelagos() throws EmptyStudentSupplyException {
        List<Archipelago> archipelagos = new LinkedList<>();
        final int numberOfArchipelagos = 12;
        for(int i = 1; i <= numberOfArchipelagos; i++) {
            Archipelago newArchipelago = new Archipelago(i);
            if(i == 1) this.motherNaturePosition = newArchipelago;
            if(i != 1 && i != numberOfArchipelagos / 2) newArchipelago.addStudent(this.studentFactory.getStudent());
            archipelagos.add(newArchipelago);
        }
        return archipelagos;
    }

    /**
     * This method initializes the schoolBoards according to the appropriate strategy depending on the number of players.
     * @throws EmptyStudentSupplyException if the studentSupply representing the bag is empty and cannot fulfill the initialization process
     * @return a List<SchoolBoard> containing the already initialized schoolBoards, students in the entrance included.
     */

    private List<SchoolBoard> initializeSchoolBoards() throws EmptyStudentSupplyException {
        return this.strategy.initializeSchoolBoards(this.studentFactory);
    }

    // Game flow methods

    /**
     * This method sets the current phase of the turn
     * @throws IllegalArgumentException if(currentPhase == null)
     */
    public void setCurrentPhase(Phase currentPhase) {
        if(currentPhase == null) throw new IllegalArgumentException();
        this.currentPhase = currentPhase;
    }

    /**
     * @requires schoolBoardId is a valid id of an existing school board in this game
     * @param schoolBoardId has to be a valid id of an existing schoolBoard
     */
    public void setCurrentPlayerSchoolBoardId(int schoolBoardId) {
        this.currentPlayerSchoolBoardId = schoolBoardId;
    }

    // Planning phase methods

    /**
     * This method gets int cloudIndex in input identifying a cloud and fills the cloud with students taken from the studentSupply
     * @throws IllegalArgumentException if the cloudIndex parameter is not valid
     * @throws FullCloudException if the cloud identified through cloudIndex is not completely empty
     * @throws EmptyStudentSupplyException if the student supply representing the bag cannot fulfill the request for students
     * @param cloudIndex is the index of the cloud to fill with students
     */
    public void fillCloud(int cloudIndex) throws FullCloudException, EmptyStudentSupplyException {
        if(cloudIndex >= this.numberOfPlayers || cloudIndex < 0) throw new IllegalArgumentException();
        List<Color> chosenCloud = this.clouds.get(cloudIndex);
        if(!chosenCloud.isEmpty()) throw new FullCloudException();
        chosenCloud.addAll(this.studentFactory.getNStudents(this.numberOfStudentsInEachCloud));
    }

    /**
     * Fills every cloud with students
     * @throws FullCloudException if one or more of the clouds are not completely empty before being refilled
     * @throws EmptyStudentSupplyException if the studentSupply cannot fulfill the demand for students to refill all the clouds
     */
    public void fillClouds() throws FullCloudException, EmptyStudentSupplyException {
        for(int cloudIndex = 0; cloudIndex < this.numberOfPlayers; cloudIndex++)
            fillCloud(cloudIndex);
    }

    /**
     * The current player grabs all the students from a cloud and puts them in the entrance
     * @param cloudIndex is the index of the cloud to pick the students from
     */
    public void grabStudentsFromCloud(int cloudIndex) throws EmptyCloudException {
        if(cloudIndex < 0 || cloudIndex >= this.numberOfPlayers) throw new IllegalArgumentException();
        if(this.clouds.get(cloudIndex).isEmpty()) throw new EmptyCloudException();
        SchoolBoard currentPlayerSchoolBoard = this.schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == this.currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0);
        List<Color> studentsGrabbed = new LinkedList<>(this.clouds.get(cloudIndex));
        this.clouds.get(cloudIndex).clear(); // Reset the cloud
        currentPlayerSchoolBoard.grabStudentsFromCloud(studentsGrabbed);
    }



    /**
     * The current player plays the given card
     * @requires school board ID exists
     * @throws IllegalArgumentException if the card parameter is null
     * @throws CardIsNotInTheDeckException if the current player does not actually own the card to be played
     * @param card the card to be played by the current player
     */
    public void playCard(Card card) throws CardIsNotInTheDeckException {
        if(card == null) throw new IllegalArgumentException();

        this.schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0)
                .playCard(card);
        this.schoolBoardIdToCardPlayedThisRound.put(currentPlayerSchoolBoardId, card);
    }

    /**
     * The current player moves a student from the entrance to the dining room
     * @throws IllegalArgumentException if(student == null)
     * @throws StudentNotInTheEntranceException if the student that the player is trying to move is not actually in the entrance
     * @param student represents a student that the player wants to move from the entrance to the diningRoom
     */
    public void moveStudentFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        if(student == null) throw new IllegalArgumentException();
        SchoolBoard currentPlayerSchoolBoard = this.schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == this.currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0);

        currentPlayerSchoolBoard.moveFromEntranceToDiningRoom(student);
    }

    /**
     * The current player moves a student from the entrance to an archipelago
     * @throws IllegalArgumentException if(student == null || archipelagoIslandCodes == null || archipelagoIslandCodes contains null)
     * @throws StudentNotInTheEntranceException if the student that the player is trying to move is not actually in the entrance
     * @param student represents a student of a certain color that the player wants to move from the entrance to an archipelago
     * @param archipelagoIslandCodes represents the islandCodes of the archipelago into which the student is being moved
     */
    public void moveStudentFromEntranceToArchipelago(Color student, List<Integer> archipelagoIslandCodes) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        if(student == null || archipelagoIslandCodes == null || archipelagoIslandCodes.contains(null))
            throw new IllegalArgumentException();

        SchoolBoard currentPlayerSchoolBoard = this.schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == this.currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0);
        currentPlayerSchoolBoard.removeStudentFromEntrance(student);
        this.archipelagos.stream()
                .filter(archipelago -> archipelago.getIslandCodes().equals(archipelagoIslandCodes))
                .collect(Collectors.toList())
                .get(0)
                .addStudent(student);
    }

    /**
     * Shifts mother nature's position one step clockwise
     */
    public void moveMotherNatureOneStepClockwise() {
        this.motherNaturePosition = this.getNextArchipelago();
    }

    private Archipelago getNextArchipelago() {
        int next = -1;
        for(int i = 0; i < this.archipelagos.size(); i++) {
            if(this.archipelagos.get(i).equals(this.motherNaturePosition))
                next = (i + 1) % this.archipelagos.size();
        }
        return this.archipelagos.get(next);
    }

    private Archipelago getPreviousArchipelago() {
        int previous = -1;
        for(int i = 0; i < this.archipelagos.size() && previous == -1; i++) {
            if(this.archipelagos.get(i).equals(this.motherNaturePosition))
                previous = i == 0 ? this.archipelagos.size() - 1 : i - 1;
        }
        return this.archipelagos.get(previous);
    }

    /**
     * Merges the archipelago mother nature is currently in with the archipelago on the left (one step counter-clockwise with respect to mother nature's position)
     * @throws NonMergeableArchipelagosException if the two archipelagos cannot be merged, see Archipelago documentation
     */
    public void mergeLeft() throws NonMergeableArchipelagosException {
        Archipelago left = getPreviousArchipelago();

        // Substitute current archipelago with the merged archipelago
        this.motherNaturePosition = Archipelago.merge(this.motherNaturePosition, left);

        // Remove left archipelago from the list
        this.archipelagos.remove(left);
    }

    /**
     * Merges the archipelago mother nature is currently in with the archipelago on the right (one step clockwise with respect to mother nature's position)
     * @throws NonMergeableArchipelagosException if the two archipelagos cannot be merged, see Archipelago documentation
     */
    public void mergeRight() throws NonMergeableArchipelagosException {
        Archipelago right = getPreviousArchipelago();

        // Substitute current archipelago with the merged archipelago
        this.motherNaturePosition = Archipelago.merge(this.motherNaturePosition, right);

        // Remove right archipelago from the list
        this.archipelagos.remove(right);
    }

    /**
     * The current player conquests the archipelago mother nature is currently in, placing a tower of his own color
     * and substituting any tower that was previously placed on that archipelago
     */
    public void conquestArchipelago() {
        TowerColor currentPlayerTowerColor = Objects.requireNonNull(this.schoolBoards.stream()
                        .filter(schoolBoard -> schoolBoard.getId() == this.currentPlayerSchoolBoardId)
                        .findFirst()
                        .orElse(null))
                .getTowerColor();

        this.motherNaturePosition.setTowerColor(currentPlayerTowerColor);
    }

    /**
     * @return the influence on the archipelago mother nature is currently in
     */
    public int getInfluence() {
        return this.strategy.getInfluence(this.schoolBoards, this.motherNaturePosition, this.currentPlayerSchoolBoardId);
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
