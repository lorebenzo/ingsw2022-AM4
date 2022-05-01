package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Card;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.GameConstants;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.*;
import it.polimi.ingsw.server.game_logic.number_of_player_strategy.NumberOfPlayersStrategy;
import it.polimi.ingsw.server.game_logic.number_of_player_strategy.NumberOfPlayersStrategyFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameState {
    private final int numberOfPlayers;
    private final NumberOfPlayersStrategy strategy;
    private final int numberOfStudentsInEachCloud;
    private final int numberOfStudentsInTheEntrance;

    // Game flow attributes
    private int currentRound; // rounds start at 0, and get incremented when all players play a turn
    //private int currentTurn; // the id of the school board owned by the current player
    //private int currentSubTurn; // the sub-turn resets at the start of each turn and gets incremented when the current player makes a move

    public enum Phase {
        PLANNING, ACTION
    }

    private Phase currentPhase;
    private final Map<Integer, Card> schoolBoardIdsToCardsPlayedThisRound;

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
        if(
                numberOfPlayers < GameConstants.MIN_NUMBER_OF_PLAYERS.value ||
                numberOfPlayers > GameConstants.MAX_NUMBER_OF_PLAYERS.value
        ) throw new IllegalArgumentException();

        this.numberOfPlayers = numberOfPlayers;
        this.strategy = NumberOfPlayersStrategyFactory.getCorrectStrategy(numberOfPlayers);

        this.numberOfStudentsInEachCloud = this.strategy.getNumberOfStudentsInEachCloud();
        this.numberOfStudentsInTheEntrance = this.strategy.getNumberOfStudentsInTheEntrance();

        this.currentRound = 0;
        //this.currentTurn = 0;
        //this.currentSubTurn = 0;
        this.schoolBoardIdsToCardsPlayedThisRound = new HashMap<>();

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
        // TODO: randomize initial mother nature position
        int motherNatureIndex = 0;

        // lambda to get, given an island index, the index of the opposite island
        Function<Integer, Integer> getOppositeIslandIndex =
                (islandIndex) -> (islandIndex + GameConstants.NUMBER_OF_ISLANDS.value / 2) % GameConstants.NUMBER_OF_ISLANDS.value;

        List<Archipelago> archipelagos = new LinkedList<>();
        for(int i = 0; i < GameConstants.NUMBER_OF_ISLANDS.value; i++) {
            Archipelago newArchipelago = new Archipelago(i);
            if(i == motherNatureIndex)
                this.motherNaturePosition = newArchipelago; // put mother nature in the first island
            if(i != motherNatureIndex && i != getOppositeIslandIndex.apply(motherNatureIndex))
                // Put a student into each island except for mother nature's island and mother nature's opposite island
                newArchipelago.addStudent(this.studentFactory.getStudent());
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
     * The current player plays the given card
     * @requires school board ID exists
     * @throws IllegalArgumentException if the card parameter is null
     * @throws CardIsNotInTheDeckException if the current player does not actually own the card to be played.
     * @throws InvalidCardPlayedException if another player already played the same card in this round, and it is not the final round.
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     * @param card the card to be played by the current player
     */
    public void playCard(Card card) throws CardIsNotInTheDeckException/*, InvalidSchoolBoardIdException*/, InvalidCardPlayedException {
        if(card == null) throw new IllegalArgumentException();

        if(this.getSchoolBoardIdsToCardsPlayedThisRound().containsValue(card) && this.getCurrentPlayerSchoolBoard().getDeck().size() > 1) throw new InvalidCardPlayedException();


        //If no other player already played the same card as the one in input in this round, or if the card in input is the last available
        this.getCurrentPlayerSchoolBoard().playCard(card);
        this.schoolBoardIdsToCardsPlayedThisRound.put(this.currentPlayerSchoolBoardId, card);

    }



    /**
     * This method gets int cloudIndex in input identifying a cloud and fills the cloud with students taken from the studentSupply
     * @throws IllegalArgumentException if the cloudIndex parameter is not valid
     * //@throws FullCloudException if the cloud identified through cloudIndex is not completely empty
     * @throws EmptyStudentSupplyException if the student supply representing the bag cannot fulfill the request for students
     * @param cloudIndex is the index of the cloud to fill with students
     */
    public void fillCloud(int cloudIndex) throws /*FullCloudException,*/ EmptyStudentSupplyException {
        if(cloudIndex >= this.numberOfPlayers || cloudIndex < 0) throw new IllegalArgumentException();
        List<Color> chosenCloud = this.clouds.get(cloudIndex);
        if(!chosenCloud.isEmpty()) throw new FullCloudException();
        chosenCloud.addAll(this.studentFactory.getNStudents(this.numberOfStudentsInEachCloud));
    }

    /**
     * Fills every cloud with students
     * //@throws FullCloudException if one or more of the clouds are not completely empty before being refilled
     * @throws EmptyStudentSupplyException if the studentSupply cannot fulfill the demand for students to refill all the clouds
     */
    public void fillClouds() throws /*FullCloudException,*/ EmptyStudentSupplyException {
        for(int cloudIndex = 0; cloudIndex < this.numberOfPlayers; cloudIndex++)
            fillCloud(cloudIndex);
    }


    /**
     * The current player grabs all the students from a cloud and puts them in the entrance
     * @param cloudIndex is the index of the cloud to pick the students from
     * @throws EmptyCloudException if the cloud is empty
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     */
    public void grabStudentsFromCloud(int cloudIndex) throws EmptyCloudException/*, InvalidSchoolBoardIdException*/ {
        if(cloudIndex < 0 || cloudIndex >= this.numberOfPlayers) throw new IllegalArgumentException();



        if(this.clouds.get(cloudIndex).isEmpty()) throw new EmptyCloudException();

        SchoolBoard currentPlayerSchoolBoard = this.getCurrentPlayerSchoolBoard();
        List<Color> studentsGrabbed = new LinkedList<>(this.clouds.get(cloudIndex));
        this.clouds.get(cloudIndex).clear(); // Reset the cloud
        currentPlayerSchoolBoard.grabStudentsFromCloud(studentsGrabbed);
    }


    /**
     * The current player moves a student from the entrance to the dining room
     * @throws IllegalArgumentException if(student == null)
     * @throws StudentNotInTheEntranceException if the student that the player is trying to move is not actually in the entrance
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     * @param student represents a student that the player wants to move from the entrance to the diningRoom
     */
    public void moveStudentFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException/*, InvalidSchoolBoardIdException*/ {
        if(student == null) throw new IllegalArgumentException();

        this.getCurrentPlayerSchoolBoard().moveFromEntranceToDiningRoom(student);
    }

    /**
     * The method gets a color in input and checks if the current player should get the professor corresponding to the inputted color and if another player should lose the corresponding professor
     * @param professor indicates the color of the professor the player should get and/or remove from another player
     * //@throws InvalidSchoolBoardIdException when there is an error with the schoolBoardId
     */
    public void assignProfessor(Color professor) /*throws InvalidSchoolBoardIdException*/ {

        if(professor == null) throw new IllegalArgumentException();

        //If the current player doesn't have the imputed professor in his professorsTable
        if(!this.getCurrentPlayerSchoolBoard().getProfessorsTable().contains(professor)){

            //Get another schoolBoard from schoolBoards that isn't the currentPlayerSchoolbard
            Optional<SchoolBoard> otherSchoolBoardMaxOptional = this.schoolBoards.stream().filter(schoolBoard -> schoolBoard.getId() != this.currentPlayerSchoolBoardId).findFirst();


            if(otherSchoolBoardMaxOptional.isPresent()){
                SchoolBoard otherSchoolBoardMax = otherSchoolBoardMaxOptional.get();
                //Find the schoolBoard that has the max number of students in the diningRoomLane corresponding to the inputted professor, apart from the currentPlayerSchoolBoard
                for (SchoolBoard schoolBoard : schoolBoards) {
                    if (!this.getCurrentPlayerSchoolBoard().equals(schoolBoard)) {
                        if(schoolBoard.getDiningRoomLaneColorToNumberOfStudents().get(professor) > otherSchoolBoardMax.getDiningRoomLaneColorToNumberOfStudents().get(professor))
                            otherSchoolBoardMax = schoolBoard;
                    }
                }

                int currentPlayerNumberOfStudentsInDiningRoomLane = this.getCurrentPlayerSchoolBoard().getDiningRoomLaneColorToNumberOfStudents().get(professor);
                int otherSchoolBoardsMaxStudentsInDiningRoomLane = otherSchoolBoardMax.getDiningRoomLaneColorToNumberOfStudents().get(professor);

                //Compare the current player's number of students in the dining room lane corresponding to the inputed professor's color with the other schoolBoard's max number of students in the dining room lane
                //If the current player has more students in the dining room lane, then he will get the professor.
                //If the current player has the same number of students in the dining room lane as the other's schoolBoard's max, then the professor will be removed from the other's schoolBoard's max.
                if(currentPlayerNumberOfStudentsInDiningRoomLane > otherSchoolBoardsMaxStudentsInDiningRoomLane)
                    this.getCurrentPlayerSchoolBoard().addProfessor(professor);
                else if(currentPlayerNumberOfStudentsInDiningRoomLane == otherSchoolBoardsMaxStudentsInDiningRoomLane){
                    otherSchoolBoardMax.removeProfessor(professor);
                }
            }

        }

    }


    /**
     * The current player moves a student from the entrance to an archipelago
     * @throws IllegalArgumentException if(student == null || archipelagoIslandCodes == null || archipelagoIslandCodes contains null || archipelagoIslandCodes is not an identifier of an actual archipelago)
     * @throws StudentNotInTheEntranceException if the student that the player is trying to move is not actually in the entrance
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     * @param student represents a student of a certain color that the player wants to move from the entrance to an archipelago
     * @param archipelagoIslandCodes represents the islandCodes of the archipelago into which the student is being moved
     */
    public void moveStudentFromEntranceToArchipelago(Color student, List<Integer> archipelagoIslandCodes) throws StudentNotInTheEntranceException/*, InvalidSchoolBoardIdException*/ {
        if(student == null || archipelagoIslandCodes == null || archipelagoIslandCodes.contains(null))
            throw new IllegalArgumentException();

        this.getCurrentPlayerSchoolBoard().removeStudentFromEntrance(student);
        Archipelago chosenArchipelago = getArchipelagoFromIslandCodes(archipelagoIslandCodes);

        if(chosenArchipelago == null) throw new IllegalArgumentException("Invalid archipelago island codes given");

        chosenArchipelago.addStudent(student);
    }

    /**
     * Shifts mother nature's position one step clockwise
     */
    private void moveMotherNatureOneStepClockwise() {
        this.motherNaturePosition = this.getNextArchipelago();
    }

    /**
     * Shifts mother nature's position by the provided numberOfSteps
     * @throws InvalidNumberOfStepsException if (numberOfSteps <= 0 || numberOfSteps > maxStepsAllowed) where maxStepsAllowed depends on the card played in the round
     * @param numberOfSteps has to be a positive integer
     */
    public void moveMotherNatureNStepsClockwise(int numberOfSteps) throws InvalidNumberOfStepsException{

        int maxStepsAllowed = this.schoolBoardIdsToCardsPlayedThisRound.get(this.currentPlayerSchoolBoardId).getValue();

        if(numberOfSteps <= 0 || numberOfSteps > maxStepsAllowed)
            throw new InvalidNumberOfStepsException();
        for (int i = 0; i < numberOfSteps; i++)
            moveMotherNatureOneStepClockwise();
    }

    /**
     * The current player conquers the archipelago mother nature is currently in, placing a tower of his own color
     * and substituting any tower that was previously placed on that archipelago
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     */
    public void conquerArchipelago(int schoolBoardId) /*throws InvalidSchoolBoardIdException*/ {
        TowerColor playerTowerColor = this.getSchoolBoardFromSchoolBoardId(schoolBoardId).getTowerColor();
        this.motherNaturePosition.setTowerColor(playerTowerColor);
    }

    /**
     * Merges the archipelago mother nature is currently in with the archipelago on the left (one step counter-clockwise with respect to mother nature's position)
     * @return true if the archipelagos merged, false otherwise.
     */
    public boolean mergeWithPrevious() /*throws NonMergeableArchipelagosException*/ {
        boolean mergePerformed = false;

        Archipelago previous = getPreviousArchipelago();

        try {
            // Substitute current archipelago with the merged archipelago
            this.motherNaturePosition = Archipelago.merge(this.motherNaturePosition, previous);
            // Remove left archipelago from the list
            this.archipelagos.remove(previous);
            mergePerformed = true;
        } catch (NonMergeableArchipelagosException ignored) {  }

        return mergePerformed;
    }

    /**
     * Merges the archipelago mother nature is currently on with the archipelago on the right (one step clockwise with respect to mother nature's position)
     * @return true if the archipelagos merged, false otherwise.
     */
    public boolean mergeWithNext() /*throws NonMergeableArchipelagosException*/ {
        boolean mergePerformed = false;

        Archipelago next = getNextArchipelago();


        try {
            // Substitute current archipelago with the merged archipelago
            this.motherNaturePosition = Archipelago.merge(this.motherNaturePosition, next);
            // Remove left archipelago from the list
            this.archipelagos.remove(next);
            mergePerformed = true;
        } catch (NonMergeableArchipelagosException ignored) {  }

        return mergePerformed;
    }


/*    *//**
     * //@return the influence on the archipelago mother nature is currently on
     *//*
    public int getPlayersInfluenceOnMotherNaturePosition(int playerSchoolBoardId) {
        return this.strategy.getInfluence(this.schoolBoards, this.motherNaturePosition, this.currentPlayerSchoolBoardId);
    }*/

    //Setters

    public void increaseRoundCount(){
        this.currentRound++;
    }

    /**
     * This method sets the current phase of the turn
     * @throws IllegalArgumentException if(currentPhase == null)
     */
    public void setCurrentPhase(Phase currentPhase) {
        if(currentPhase == null) throw new IllegalArgumentException();
        this.currentPhase = currentPhase;
    }

    /**
     * @param schoolBoardId has to be a valid id of an existing schoolBoard
     */
    public void setCurrentPlayerSchoolBoardId(int schoolBoardId) {
        if(!this.getSchoolBoardIds().contains(schoolBoardId)) throw new InvalidSchoolBoardIdException("Invalid schoolboardId in input.");

        this.currentPlayerSchoolBoardId = schoolBoardId;
    }




    // Getters


    //Private getters

    /**
     * This method returns the corresponding schoolBoard to the inputted schoolBoardId
     * @param schoolBoardId has to be an existing schoolBoardId
     * @return the reference to the schoolBoard corresponding to the inputted schoolBoardId
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     */
    private SchoolBoard getSchoolBoardFromSchoolBoardId(int schoolBoardId) /*throws InvalidSchoolBoardIdException */{
        Optional<SchoolBoard> requestedSchoolBoard =
                // get all school boards
                this.schoolBoards.stream()
                        // filter out the ones that don't match currentPlayerSchoolBoardId
                        .filter(schoolBoard -> schoolBoard.getId() == schoolBoardId)
                        // get the first result
                        .findFirst();

        if(requestedSchoolBoard.isEmpty())
            throw new InvalidSchoolBoardIdException("Could not find a school board that matches the inputted player's schoolBoard ID");

        return requestedSchoolBoard.get();
    }


    private Archipelago getPreviousArchipelago() {
        int pos =  this.archipelagos.indexOf(this.motherNaturePosition);
        int previous = pos == 0 ? this.archipelagos.size() - 1 : pos - 1;

        return this.archipelagos.get(previous);
    }

    private Archipelago getNextArchipelago() {
        int next = (this.archipelagos.indexOf(this.motherNaturePosition) + 1) % this.archipelagos.size();
        return this.archipelagos.get(next);
    }


    private Archipelago getArchipelagoFromIslandCodes(List<Integer> archipelagoIslandCodes){
        return this.archipelagos.stream()
                // Filter out archipelagos that don't match archipelagoIslandCodes
                .filter(archipelago -> archipelago.getIslandCodes().equals(archipelagoIslandCodes))
                .findFirst()
                .orElse(null);
    }

    private SchoolBoard getCurrentPlayerSchoolBoard()/* throws InvalidSchoolBoardIdException*/ {
        return this.getSchoolBoardFromSchoolBoardId(this.currentPlayerSchoolBoardId);
    }


    //Public Getters

    /**
     * This method gets an archipelago in input and returns a map where every entry links a schoolBoard with its influence on the inputed archipelago
     * @param archipelagoIslandCodes is a List<Integer> uniquely identifying an archipelago
     * @return a Map<Integer, Integer> where the key is the schoolBoardId and the value is the influence on the inputed archipelago
     */
    public Map<Integer, Integer> getInfluence(List<Integer> archipelagoIslandCodes){
        return this.strategy.getInfluence(this.schoolBoards,this.getArchipelagoFromIslandCodes(archipelagoIslandCodes));
    }

    public int getNumberOfStudentsInTheEntrance(){
        return this.getCurrentPlayerSchoolBoard().getStudentsInTheEntrance().size();
    }

    public int getInitialNumberOfStudentsInTheEntrance(){
        return this.numberOfStudentsInTheEntrance;
    }

    public int getNumberOfMovableStudents() {
        return this.numberOfStudentsInEachCloud;
    }

    public Phase getCurrentPhase() {
        return this.currentPhase;
    }

    public List<Integer> getMotherNaturePositionIslandCodes() {
        return this.motherNaturePosition.getIslandCodes();
    }

    public List<List<Color>> getClouds() {
        return new LinkedList<>(this.clouds);
    }

    public int getNumberOfPlayers() {
        return this.numberOfPlayers;
    }

    public Set<Integer> getSchoolBoardIds() {
        return this.schoolBoards.stream()
                .map(SchoolBoard::getId)
                .collect(Collectors.toSet());
    }

    public Map<Integer, Card> getSchoolBoardIdsToCardsPlayedThisRound() {
        return new HashMap<>(this.schoolBoardIdsToCardsPlayedThisRound);
    }

    public void resetSchoolBoardIdsToCardsPlayerThisRound(){
        this.schoolBoardIdsToCardsPlayedThisRound.clear();
    }

    public int getCurrentPlayerSchoolBoardId() {
        return this.currentPlayerSchoolBoardId;
    }




    //Created for testing - could be useful or dangerous

    public void setMotherNaturePositionForTesting(Archipelago motherNaturePosition) {
        this.motherNaturePosition = motherNaturePosition;
    }

    public SchoolBoard getCurrentPlayerSchoolBoardForTesting() /*throws InvalidSchoolBoardIdException*/ {
        SchoolBoard currentPlayerSchoolBoard =
                // get all school boards
                this.schoolBoards.stream()
                        // filter out the ones that don't match currentPlayerSchoolBoardId
                        .filter(schoolBoard -> schoolBoard.getId() == this.currentPlayerSchoolBoardId)
                        // get the first result
                        .findFirst()
                        // if there is no result, set null
                        .orElse(null);

        if(currentPlayerSchoolBoard == null)
            throw new InvalidSchoolBoardIdException("Could not find a schoolboard that matches the current player's SchoolBoard ID");

        return currentPlayerSchoolBoard;
    }

    public List<Archipelago> getArchipelagosForTesting() {
        return archipelagos;
    }

    public Map<Integer, Card> getSchoolBoardIdsToCardsPlayedThisRoundForTesting() {
        return this.schoolBoardIdsToCardsPlayedThisRound;
    }


    /*private void setCurrentPlayerProfessor(Color professor) *//*throws InvalidSchoolBoardIdException*//*{

        for (SchoolBoard schoolBoard : schoolBoards) {
            if(!schoolBoard.equals(this.getCurrentPlayerSchoolBoard())){
                if(schoolBoard.getProfessorsTable().contains(professor))
                    schoolBoard.removeProfessor(professor);
            }
        }

        this.getCurrentPlayerSchoolBoard().addProfessor(professor);

    }*/

}
