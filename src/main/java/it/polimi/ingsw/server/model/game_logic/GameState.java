package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidCardPlayedException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidNumberOfStepsException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.*;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.NumberOfPlayersStrategy;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.NumberOfPlayersStrategyFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameState implements GameStateCommonInterface {
    protected final int numberOfPlayers;
    protected NumberOfPlayersStrategy strategy;
    private final int numberOfStudentsInEachCloud;
    private final int numberOfStudentsInTheEntrance;
    private final int numberOfTowers;

    // Game flow attributes
    private int currentRound; // rounds start at 0, and get incremented when all players play a turn
    private List<Integer> roundOrder;
    private Iterator<Integer> roundIterator;
    private ActionPhaseSubTurn actionPhaseSubTurn;

    private Phase currentPhase;
    protected final Map<Integer, Card> schoolBoardIdsToCardsPlayedThisRound;

    protected final List<Archipelago> archipelagos;
    protected final List<SchoolBoard> schoolBoards;
    private final List<List<Color>> clouds;

    protected final StudentFactory studentFactory;
    protected Archipelago motherNaturePosition;

    protected int currentPlayerSchoolBoardId;

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
        this.chooseStrategy();


        this.numberOfStudentsInEachCloud = this.strategy.getNumberOfStudentsInEachCloud();
        this.numberOfStudentsInTheEntrance = this.strategy.getNumberOfStudentsInTheEntrance();
        this.numberOfTowers = this.strategy.getNumberOfTowers();

        this.currentRound = 0;
        this.actionPhaseSubTurn = ActionPhaseSubTurn.STUDENTS_TO_MOVE;



        //this.currentTurn = 0;
        //this.currentSubTurn = 0;
        this.schoolBoardIdsToCardsPlayedThisRound = new HashMap<>();

        this.studentFactory = new StudentFactory();
        try {
            this.archipelagos = this.initializeArchipelagos();
            this.schoolBoards = this.initializeSchoolBoards();
            this.clouds = this.initializeClouds();
        } catch (EmptyStudentSupplyException e) {
            throw new GameStateInitializationFailureException();
        }

        //Preparation of the roundOrder that will support the turns
        this.roundOrder = this.schoolBoards.stream().map(SchoolBoard::getId).toList();
        this.roundIterator = this.getRoundOrder().listIterator();

    }

    protected void chooseStrategy(){
        this.strategy = NumberOfPlayersStrategyFactory.getCorrectStrategy(numberOfPlayers);
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
            Archipelago newArchipelago = this.createArchipelago(i);
            if(i == motherNatureIndex)
                this.motherNaturePosition = newArchipelago; // put mother nature in the first island
            if(i != motherNatureIndex && i != getOppositeIslandIndex.apply(motherNatureIndex))
                // Put a student into each island except for mother nature's island and mother nature's opposite island
                newArchipelago.addStudent(this.studentFactory.getStudent());
            archipelagos.add(newArchipelago);
        }
        return archipelagos;
    }

    protected Archipelago createArchipelago(int code){
        return new Archipelago(code);
    }

    /**
     * This method initializes the schoolBoards according to the appropriate strategy depending on the number of players.
     * @throws EmptyStudentSupplyException if the studentSupply representing the bag is empty and cannot fulfill the initialization process
     * @return a List<SchoolBoard> containing the already initialized schoolBoards, students in the entrance included.
     */
    protected List<SchoolBoard> initializeSchoolBoards() throws EmptyStudentSupplyException {
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
    public void playCard(Card card) throws CardIsNotInTheDeckException, InvalidCardPlayedException {
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
    public void fillCloud(int cloudIndex) throws EmptyStudentSupplyException {
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
    public void fillClouds() throws EmptyStudentSupplyException {
        for(int cloudIndex = 0; cloudIndex < this.numberOfPlayers; cloudIndex++)
            fillCloud(cloudIndex);
    }


    /**
     * The current player grabs all the students from a cloud and puts them in the entrance
     * @param cloudIndex is the index of the cloud to pick the students from
     * @throws EmptyCloudException if the cloud is empty
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     */
    public void grabStudentsFromCloud(int cloudIndex) throws EmptyCloudException {
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
    public void moveStudentFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        if(student == null) throw new IllegalArgumentException();

        this.getCurrentPlayerSchoolBoard().moveFromEntranceToDiningRoom(student);
    }

    /**
     * The method gets a color in input and checks if the current player should get the professor corresponding to the inputted color and if another player should lose the corresponding professor
     * @param professor indicates the color of the professor the player should get and/or remove from another player
     * //@throws InvalidSchoolBoardIdException when there is an error with the schoolBoardId
     */
    public Map<Color, Integer> assignProfessor(Color professor) /*throws InvalidSchoolBoardIdException*/ {
        Map<Color, Integer> professorToPreviousOwnerMap = new HashMap<>();

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

                //Compare the current player's number of students in the dining room lane corresponding to the inputted professor's color with the other schoolBoard's max number of students in the dining room lane
                //If the current player has more students in the dining room lane, then he will get the professor.
                //If the current player has the same number of students in the dining room lane as the other's schoolBoard's max, then the professor will be removed from the other's schoolBoard's max.
                if(this.compareCurrentPlayersStudentsNumberWithOthersMax(currentPlayerNumberOfStudentsInDiningRoomLane,otherSchoolBoardsMaxStudentsInDiningRoomLane)){
                    this.getCurrentPlayerSchoolBoard().addProfessor(professor);
                    otherSchoolBoardMax.removeProfessor(professor);
                    professorToPreviousOwnerMap.put(professor, otherSchoolBoardMax.getId());
                }
            }

        }

        return professorToPreviousOwnerMap;
    }

    protected boolean compareCurrentPlayersStudentsNumberWithOthersMax(int currentPlayerNumberOfStudentsInDiningRoomLane, int otherSchoolBoardsMaxStudentsInDiningRoomLane){
        return currentPlayerNumberOfStudentsInDiningRoomLane > otherSchoolBoardsMaxStudentsInDiningRoomLane;
    }

    /**
     * The current player moves a student from the entrance to an archipelago
     * @throws IllegalArgumentException if(student == null || archipelagoIslandCodes == null || archipelagoIslandCodes contains null || archipelagoIslandCodes is not an identifier of an actual archipelago)
     * @throws StudentNotInTheEntranceException if the student that the player is trying to move is not actually in the entrance
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     * @param student represents a student of a certain color that the player wants to move from the entrance to an archipelago
     * @param archipelagoIslandCodes represents the islandCodes of the archipelago into which the student is being moved
     */
    public void moveStudentFromEntranceToArchipelago(Color student, List<Integer> archipelagoIslandCodes) throws StudentNotInTheEntranceException {
        if(student == null || archipelagoIslandCodes == null || archipelagoIslandCodes.contains(null))
            throw new IllegalArgumentException();

        this.getCurrentPlayerSchoolBoard().removeStudentFromEntrance(student);
        Optional<Archipelago> chosenArchipelago = getArchipelagoFromIslandCodes(archipelagoIslandCodes);
        if(chosenArchipelago.isEmpty()) throw new IllegalArgumentException("Invalid archipelago island codes given");

        chosenArchipelago.get().addStudent(student);
    }

    /**
     * Shifts mother nature's position one step clockwise
     */
    private void moveMotherNatureOneStepClockwise() {
        this.motherNaturePosition = this.getNextArchipelago();
    }

    protected int getAllowedStepsNumber(){
        return this.schoolBoardIdsToCardsPlayedThisRound.get(this.currentPlayerSchoolBoardId).getSteps();
    }

    /**
     * Shifts mother nature's position by the provided numberOfSteps
     * @throws InvalidNumberOfStepsException if (numberOfSteps <= 0 || numberOfSteps > maxStepsAllowed) where maxStepsAllowed depends on the card played in the round
     * @param numberOfSteps has to be a positive integer
     */
    public void moveMotherNatureNStepsClockwise(int numberOfSteps) throws InvalidNumberOfStepsException {

        if(numberOfSteps <= 0 || numberOfSteps > this.getAllowedStepsNumber())
            throw new InvalidNumberOfStepsException();
        for (int i = 0; i < numberOfSteps; i++)
            this.moveMotherNatureOneStepClockwise();
    }

    /**
     * The current player conquers the archipelago mother nature is currently in, placing a tower of his own color
     * and substituting any tower that was previously placed on that archipelago
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     */
    public void conquerArchipelago(int schoolBoardId) {
        TowerColor playerTowerColor = this.getSchoolBoardFromSchoolBoardId(schoolBoardId).getTowerColor();
        this.motherNaturePosition.setTowerColor(playerTowerColor);
    }


    /**
     * Merges the archipelago mother nature is currently in with the archipelago on the left (one step counter-clockwise with respect to mother nature's position)
     * @return true if the archipelagos merged, false otherwise.
     */
    public boolean mergeWithPrevious() {
        boolean mergePerformed;
        Archipelago previous = getPreviousArchipelago();

        // Substitute current archipelago with the merged archipelago
        mergePerformed = this.motherNaturePosition.merge(previous);
        // Remove previous archipelago from the list
        if(mergePerformed){
            this.archipelagos.remove(previous);
        }


        return mergePerformed;
    }

    /**
     * Merges the archipelago mother nature is currently on with the archipelago on the right (one step clockwise with respect to mother nature's position)
     * @return true if the archipelagos merged, false otherwise.
     */
    public boolean mergeWithNext() {
        boolean mergePerformed;
        Archipelago next = getNextArchipelago();

        // Substitute current archipelago with the merged archipelago
        mergePerformed = this.motherNaturePosition.merge(next);
        // Remove the next archipelago from the list
        if(mergePerformed)
            this.archipelagos.remove(next);

        return mergePerformed;
    }

    /**
     *
     * @return a map schoolBoardId -> isWinner
     */
    public Map<Integer, Boolean> checkWinners(boolean isRoundTerminated) {
        Map<Integer, Boolean> schoolBoardIdToIsWinnerMap = new HashMap<>();

        // Init map
        for(var schoolBoard : this.schoolBoards)
            schoolBoardIdToIsWinnerMap.put(schoolBoard.getId(), false);


        var schoolboardToNumberOfTowersPlaced = new HashMap<SchoolBoard, Integer>();

        // Init map
        for(var schoolboard : this.schoolBoards)
            schoolboardToNumberOfTowersPlaced.put(schoolboard, 0);


        if(isGameOver(isRoundTerminated)) {
            for (var schoolboard : this.schoolBoards) {
                for (var archipelago : this.archipelagos) {
                    if (archipelago.getTowerColor().equals(schoolboard.getTowerColor())) {
                        schoolboardToNumberOfTowersPlaced.put(
                                schoolboard,
                                schoolboardToNumberOfTowersPlaced.get(schoolboard) + archipelago.getIslandCodes().size()
                        );
                    }
                }
            }

            var maxTowersPlaced = schoolboardToNumberOfTowersPlaced
                    .keySet()
                    .stream()
                    .mapToInt(schoolboardToNumberOfTowersPlaced::get)
                    .max();

            // List of winner schoolboards
            List<SchoolBoard> winnerSchoolBoards = new LinkedList<>();

            // Filter by maximum number of towers
            for (var schoolboard : schoolboardToNumberOfTowersPlaced.keySet())
                if (schoolboardToNumberOfTowersPlaced.get(schoolboard) == maxTowersPlaced.getAsInt())
                    winnerSchoolBoards.add(schoolboard);

            if (winnerSchoolBoards.size() > 1) {
                if (this.numberOfPlayers != GameConstants.MAX_NUMBER_OF_PLAYERS.value) {
                    // Filter by number of professors
                    var maxNumberOfProfessors = winnerSchoolBoards
                            .stream()
                            .mapToInt(schoolBoard -> schoolBoard.getProfessors().size())
                            .max();

                    winnerSchoolBoards = winnerSchoolBoards
                            .stream()
                            .filter(schoolBoard -> schoolBoard.getProfessors().size() == maxNumberOfProfessors.getAsInt())
                            .toList();
                } else {
                    var whiteSchoolBoards = winnerSchoolBoards
                            .stream()
                            .filter(schoolBoard -> schoolBoard.getTowerColor().equals(TowerColor.WHITE));

                    var blackSchoolBoards = winnerSchoolBoards
                            .stream()
                            .filter(schoolBoard -> schoolBoard.getTowerColor().equals(TowerColor.BLACK));

                    var whiteProfessors = whiteSchoolBoards
                            .mapToInt(schoolBoard -> schoolBoard.getProfessors().size())
                            .sum();

                    var blackProfessors = blackSchoolBoards
                            .mapToInt(schoolBoard -> schoolBoard.getProfessors().size())
                            .sum();

                    winnerSchoolBoards = (whiteProfessors >= blackProfessors ? whiteSchoolBoards : blackSchoolBoards).toList();
                }
            }

            for (var schoolBoard : winnerSchoolBoards)
                schoolBoardIdToIsWinnerMap.put(schoolBoard.getId(), true);
        }

        return schoolBoardIdToIsWinnerMap;
    }

    public boolean isGameOver(boolean isLastRound) {
        // There are only 3 archipelagos left
        if(this.archipelagos.size() <= 3)
            return true;

        // A player places his last tower
        for(var schoolboard : this.schoolBoards) {
            TowerColor towerColor = schoolboard.getTowerColor();
            var archipelgosWithThisTowerColor =
                    this.archipelagos
                            .stream()
                            .filter(archipelago -> archipelago.getTowerColor().equals(towerColor))
                            .toList();

            var towersPlaced = 0;

            for(var archipelago : archipelgosWithThisTowerColor)
                towersPlaced += archipelago.getIslandCodes().size();

            if(towersPlaced >= this.numberOfTowers) return true;
        }

        // Student supply is finished or there is at least a player that finished the cards
        if(isLastRound)
            return this.studentFactory.isEmpty() ||
                    this.schoolBoards.stream()
                            .anyMatch(schoolBoard -> schoolBoard.getDeck().isEmpty());

        return false;
    }


    //Setters


    public void setRoundOrder(List<Integer> roundOrder) {
        this.roundOrder = roundOrder;
    }

    public void resetRoundIterator() {
        this.roundIterator = this.getRoundOrder().listIterator();
    }

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
    protected SchoolBoard getSchoolBoardFromSchoolBoardId(int schoolBoardId) /*throws InvalidSchoolBoardIdException */{
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


    protected Archipelago getPreviousArchipelago() {
        int pos =  this.archipelagos.indexOf(this.motherNaturePosition);
        int previous = pos == 0 ? this.archipelagos.size() - 1 : pos - 1;

        return this.archipelagos.get(previous);
    }

    private Archipelago getNextArchipelago() {
        int next = (this.archipelagos.indexOf(this.motherNaturePosition) + 1) % this.archipelagos.size();
        return this.archipelagos.get(next);
    }


    protected Optional<Archipelago> getArchipelagoFromIslandCodes(List<Integer> archipelagoIslandCodes){
        return this.archipelagos.stream()
                // Filter out archipelagos that don't match archipelagoIslandCodes
                .filter(archipelago -> archipelago.getIslandCodes().equals(archipelagoIslandCodes))
                .findFirst();
    }

    protected Optional<Archipelago> getArchipelagoFromSingleIslandCode(int archipelagoIslandCode){
        return this.archipelagos.stream()
                // Filter out archipelagos that don't match archipelagoIslandCodes
                .filter(archipelago -> archipelago.getIslandCodes().contains(archipelagoIslandCode))
                .findFirst();
    }


    protected SchoolBoard getCurrentPlayerSchoolBoard()/* throws InvalidSchoolBoardIdException*/ {
        return this.getSchoolBoardFromSchoolBoardId(this.currentPlayerSchoolBoardId);
    }




    //Public Getters

    /**
     * This method gets an archipelago in input and returns a map where every entry links a schoolBoard with its influence on the inputted archipelago
     * @param archipelagoIslandCodes is a List<Integer> uniquely identifying an archipelago
     * @return a Map<Integer, Integer> where the key is the schoolBoardId and the value is the influence on the inputted archipelago
     */
    public Optional<Map<Integer, Integer>> getInfluence(List<Integer> archipelagoIslandCodes){
        Optional<Archipelago> chosenArchipelago = this.getArchipelagoFromIslandCodes(archipelagoIslandCodes);
        return chosenArchipelago.map(archipelago -> this.strategy.getInfluence(this.schoolBoards, archipelago));
    }

    public List<Integer> getRoundOrder() {
        return new LinkedList<>(roundOrder);
    }

    //TODO check exposed reference
    public Iterator<Integer> getRoundIterator() {
        return roundIterator;
    }

    public boolean isLastTurnInThisRound(){
        return !this.roundIterator.hasNext();
    }

    public int getNextTurn() { return this.roundIterator.next(); }


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

    public void setActionPhaseSubTurn(ActionPhaseSubTurn actionPhaseSubTurn) {
        this.actionPhaseSubTurn = actionPhaseSubTurn;
    }

    public ActionPhaseSubTurn getActionPhaseSubTurn() {
        return this.actionPhaseSubTurn;
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

    public LightGameState lightify(){
        return new LightGameState(
                this.archipelagos,
                this.schoolBoards,
                this.clouds,
                this.currentPlayerSchoolBoardId,
                this.currentPhase,
                this.roundOrder,
                this.motherNaturePosition
        );
    }



    /**
     * This method verifies if there is a schoolBoard that is more influent than all the others on the archipelago on which motherNature is,
     * and returns its schoolBoardId
     * @return an integer representing the schoolBoardId of the most influent player on the archipelago on which motherNature is
     */
    public Optional<Integer> getMostInfluentSchoolBoardId(List<Integer> archipelagoIslandCodes){
        var influenceMap = this.getInfluence(archipelagoIslandCodes);
        List<Map.Entry<Integer, Integer>> orderedPlayersInfluences;

        if(influenceMap.isPresent()){
            orderedPlayersInfluences = influenceMap.get().entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) //sorts the map using the influences in descending order
                    .collect(Collectors.toList());
        }
        else
            return Optional.empty();



        //If the number of players is 2 or 3, the most influent is calculated between the most influent and the second most influent.

        if(this.getNumberOfPlayers() == 2 || this.getNumberOfPlayers() == 3){
            if(orderedPlayersInfluences.get(0).getValue() > orderedPlayersInfluences.get(1).getValue())
                return Optional.of(orderedPlayersInfluences.get(0).getKey());
            else
                return Optional.empty();
        }
        //if the number of players is 4, the most influent is calculated between the most influent and the third most influent, since the second most influent is certainly a teammate.
        else {
            if(orderedPlayersInfluences.get(0).getValue() > orderedPlayersInfluences.get(2).getValue())
                return Optional.of(orderedPlayersInfluences.get(0).getKey());
            else
                return Optional.empty();
        }
    }



}
