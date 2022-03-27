package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Card;
import it.polimi.ingsw.server.game_logic.enums.Color;
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
     *
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
        final int numberOfArchipelagos = 12;
        final Queue<Color> randomStudents = this.studentFactory.getStudentsForArchipelagosInitialization();
        for(int i = 1; i <= numberOfArchipelagos; i++) {
            Archipelago newArchipelago = new Archipelago(i);
            if(i == 1) this.motherNaturePosition = newArchipelago;
            if(i != 1 && i != numberOfArchipelagos / 2) newArchipelago.addStudent(randomStudents.poll());
            archipelagos.add(newArchipelago);
        }
        return archipelagos;
    }

    private List<SchoolBoard> initializeSchoolBoards() throws EmptyStudentSupplyException {
        return this.strategy.initializeSchoolBoards(this.studentFactory);
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
     * Fills every cloud with students
     */
    public void fillClouds() throws FullCloudException, EmptyStudentSupplyException {
        for(int cloudIndex = 0; cloudIndex < this.numberOfPlayers; cloudIndex++)
            fillCloud(cloudIndex);
    }

    /**
     * The current player grabs all the students from a cloud and puts them in the entrance
     */
    public void grabStudentsFromCloud(int cloudIndex) {
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
     * @requires the current player actually owns that card && card != null && school board ID exists
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
     *
     * @param student any student in the entrance of the current player
     * @throws StudentNotInTheEntranceException if the current player does not have a student of that color in the entrance
     * @throws FullDiningRoomLaneException if the lane of the current player's dining room corresponding to that student color is already full
     */
    public void moveStudentFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        SchoolBoard currentPlayerSchoolBoard = this.schoolBoards.stream()
                .filter(schoolBoard -> schoolBoard.getId() == this.currentPlayerSchoolBoardId)
                .collect(Collectors.toList())
                .get(0);

        currentPlayerSchoolBoard.moveFromEntranceToDiningRoom(student);
    }

    /**
     * The current player moves a student from the entrance to an archipelago
     *
     * @param student any student in the entrance of the current player
     * @param archipelagoIslandCodes the identifier of the archipelago the current player wants to move his student in
     * @throws StudentNotInTheEntranceException if the current player does not have a student of that color in the entrance
     */
    public void moveStudentFromEntranceToArchipelago(Color student, List<Integer> archipelagoIslandCodes) throws StudentNotInTheEntranceException {
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

    /**
     *
     * @return the influence of the current player on the archipelago mother nature is currently in
     */
    public int getInfluence() {
        return this.strategy.getInfluence(this.schoolBoards, this.motherNaturePosition, this.currentPlayerSchoolBoardId);
    }

    /**
     *
     * @param islandCodes the identifier of the archipelago, which consists in a list containing all the IDs of the islands contained into the archipelago
     * @param currentPlayerSchoolBoardId the identifier of the school board owned by the player we are calculating the influence of
     * @throws InvalidArchipelagoIdException if the islandCodes do not match any archipelago in this game
     * @return the influence of the desired player on the archipelago identified by the islandCodes
     */
    public int getInfluenceOnArchipelago(List<Integer> islandCodes, int currentPlayerSchoolBoardId) throws InvalidArchipelagoIdException {
        Archipelago archipelago = this.archipelagos.stream()
                .filter(arch -> arch.getIslandCodes().equals(islandCodes))
                .findFirst()
                .orElse(null);
        if(archipelago == null) throw new InvalidArchipelagoIdException();

        return this.strategy.getInfluence(this.schoolBoards, archipelago, currentPlayerSchoolBoardId);
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
