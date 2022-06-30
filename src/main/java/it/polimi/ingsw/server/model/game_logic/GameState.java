package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidCardPlayedException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidNumberOfStepsException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.event_sourcing.Aggregate;
import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.model.game_logic.enums.*;
import it.polimi.ingsw.server.model.game_logic.events.*;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.NumberOfPlayersStrategy;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.NumberOfPlayersStrategyFactory;
import it.polimi.ingsw.server.model.game_logic.utils.Randomizer;
import it.polimi.ingsw.server.repository.exceptions.DBQueryException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameState extends Aggregate implements GameStateCommonInterface {
    protected int numberOfPlayers;
    protected NumberOfPlayersStrategy strategy;
    private int numberOfTowers;

    private int numberOfStudentsInEachCloud;
    private int numberOfStudentsInTheEntrance;

    // Game flow attributes
    protected Round round;

    protected Map<Integer, Card> schoolBoardIdsToCardPlayedThisRound;
    protected Map<Integer, Card> schoolBoardIdsToLastCardPlayed;

    protected List<Archipelago> archipelagos;
    protected List<SchoolBoard> schoolBoards;
    protected List<List<Color>> clouds;

    public StudentFactory studentFactory;
    protected Archipelago motherNaturePosition;

    protected int currentPlayerSchoolBoardId;


    public GameState(UUID uuid) {
        this.id = uuid;
    }

    /**
     * @throws IllegalArgumentException if the argument representing the number of players is not between 2 and 4
     * @throws GameStateInitializationFailureException if there is a problem in the initialization of the archipelagos
     *                                                                                             or the schoolBoards,
     *                                                                                             or the clouds.
     * @param numberOfPlayers number of players in the game, must be between 2 (inclusive) and 4 (inclusive)
     */
    public GameState(int numberOfPlayers) throws GameStateInitializationFailureException {
        UUID parentUuid = UUID.randomUUID();

        try {
            if(repository!= null)
                repository.storeAggregate(this.id, this.getClass().getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        var event = new InitGameStateEvent(parentUuid, numberOfPlayers);

        this.updateSeed(event);
        this.initGameState(event);
        this.addEvent(event);
        try {
            this.fillClouds();

            this.createSnapshot();
        } catch(Exception ignored) {}
    }

    public void initGameState(InitGameStateEvent event) throws GameStateInitializationFailureException {
        var numberOfPlayers = event.numberOfPlayers;
        var parentUUID = event.parentEvent;
        if(
                numberOfPlayers < GameConstants.MIN_NUMBER_OF_PLAYERS.value ||
                numberOfPlayers > GameConstants.MAX_NUMBER_OF_PLAYERS.value
        ) throw new IllegalArgumentException();

        this.numberOfPlayers = numberOfPlayers;
        this.chooseStrategy();

        this.numberOfStudentsInEachCloud = this.strategy.getNumberOfStudentsInEachCloud();
        this.numberOfStudentsInTheEntrance = this.strategy.getNumberOfStudentsInTheEntrance();

        this.schoolBoardIdsToCardPlayedThisRound = new HashMap<>();
        this.schoolBoardIdsToLastCardPlayed = new HashMap<>();
        this.studentFactory = new StudentFactory();
        try {
            this.archipelagos = this.initializeArchipelagos();
            this.schoolBoards = this.initializeSchoolBoards();
            this.clouds = this.initializeClouds();
        } catch (EmptyStudentSupplyException e) {
            throw new GameStateInitializationFailureException();
        }

        //Preparation of the roundOrder that will support the turns
        this.round = new Round(this.schoolBoards.stream().map(SchoolBoard::getId).toList());
        this.currentPlayerSchoolBoardId = this.round.next();

    }

    public List<Integer> getSchoolBoardIDFromTowerColor(TowerColor towerColor) {
        return this.schoolBoards.stream()
                .filter(s -> s.getTowerColor() == towerColor)
                .map(SchoolBoard::getId)
                .toList();
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
        Archipelago newArchipelago;
        int motherNatureIndex = 0;

        // lambda to get, given an island index, the index of the opposite island
        Function<Integer, Integer> getOppositeIslandIndex =
                (islandIndex) -> (islandIndex + GameConstants.NUMBER_OF_ISLANDS.value / 2) % GameConstants.NUMBER_OF_ISLANDS.value;

        List<Archipelago> archipelagos = new LinkedList<>();

        List<Color> studentsForArchipelagos = new LinkedList<>();

        for (Color color: Color.values()) {
            studentsForArchipelagos.add(color);
            studentsForArchipelagos.add(color);
        }

        Collections.shuffle(studentsForArchipelagos);
        ListIterator<Color> studentsForArchipelagosIterator = studentsForArchipelagos.listIterator();

        for(int i = 0; i < GameConstants.NUMBER_OF_ISLANDS.value; i++) {
            newArchipelago = this.createArchipelago(i);
            if(i != motherNatureIndex && i != getOppositeIslandIndex.apply(motherNatureIndex))
                // Put a student into each island except for mother nature's island and mother nature's opposite island
                newArchipelago.addStudent(this.studentFactory.getStudent(studentsForArchipelagosIterator.next()));
            archipelagos.add(newArchipelago);
        }

        this.motherNaturePosition = archipelagos.get(motherNatureIndex);

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
     * @requires Card to be played
     * @throws IllegalArgumentException if the card parameter is null
     * @throws CardIsNotInTheDeckException if the current player does not actually own the card to be played.
     * @throws InvalidCardPlayedException if another player already played the same card in this round, and it is not the final round.
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     * @param card the card to be played by the current player
     */
    public void playCard(Card card) throws InvalidCardPlayedException, CardIsNotInTheDeckException {
        if(card == null) throw new IllegalArgumentException();

        UUID parentUuid = UUID.randomUUID();

        var event = new PlayCardEvent(parentUuid, card);

        this.updateSeed(event);
        this.playCardHandler(event);
        this.addEvent(event);
    }

    public void playCardHandler(PlayCardEvent event) throws CardIsNotInTheDeckException, InvalidCardPlayedException {
        var card = event.card;
        if(this.getSchoolBoardIdsToCardPlayedThisRound().containsValue(card) && this.getCurrentPlayerSchoolBoard().getDeck().size() > 1) throw new InvalidCardPlayedException();


        //If no other player already played the same card as the one in input in this round, or if the card in input is the last available
        this.getCurrentPlayerSchoolBoard().playCard(card);
        this.schoolBoardIdsToCardPlayedThisRound.put(this.currentPlayerSchoolBoardId, card);
        this.schoolBoardIdsToLastCardPlayed.put(this.currentPlayerSchoolBoardId,card);

    }

    public void apply(Event event) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(this.studentFactory != null)
            Randomizer.setSeed(event.id.getLeastSignificantBits());
        Method handler = this.getClass().getMethod(event.handlerName, event.getClass());
        handler.invoke(this, event);
    }


    /**
     * This method gets int cloudIndex in input identifying a cloud and fills the cloud with students taken from the studentSupply
     * @throws IllegalArgumentException if the cloudIndex parameter is not valid
     * @throws EmptyStudentSupplyException if the student supply representing the bag cannot fulfill the request for students
     * @param cloudIndex is the index of the cloud to fill with students
     */
    public void fillCloud(int cloudIndex) throws EmptyStudentSupplyException, IllegalArgumentException {
        UUID parentUuid = UUID.randomUUID();
        var event = new FillCloudEvent(parentUuid, cloudIndex);

        this.updateSeed(event);
        this.fillCloudHandler(event);
        this.addEvent(event);
    }

    private void addEvent(Event event) {
        try {
            if(repository!= null)
                repository.addEvent(this.id, event, ++this.version);
        } catch (DBQueryException e) {
            e.printStackTrace();
        }
    }

    private void updateSeed(Event event) {
        Randomizer.setSeed(event.id.getLeastSignificantBits());
    }

    public void fillCloudHandler(FillCloudEvent event) throws EmptyStudentSupplyException, IllegalArgumentException {
        var cloudIndex = event.cloudIndex;

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
     */
    public void grabStudentsFromCloud(int cloudIndex) throws EmptyCloudException {
        UUID parentUuid = UUID.randomUUID();
        var event = new GrabStudentsFromCloudEvent(parentUuid, cloudIndex);

        this.updateSeed(event);
        this.grabStudentsFromCloudHandler(event);
        this.addEvent(event);
    }

    public void grabStudentsFromCloudHandler(GrabStudentsFromCloudEvent event) throws EmptyCloudException {
        var cloudIndex = event.cloudIndex;

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
     * @param student represents a student that the player wants to move from the entrance to the diningRoom
     */
    public void moveStudentFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        if(student == null) throw new IllegalArgumentException();

        UUID parentUuid = UUID.randomUUID();
        var event = new MoveStudentsFromEntranceToDiningEvent(parentUuid, student);

        this.updateSeed(event);
        this.moveStudentFromEntranceToDiningHandler(event);
        this.addEvent(event);
    }

    public void moveStudentFromEntranceToDiningHandler(MoveStudentsFromEntranceToDiningEvent event) throws StudentNotInTheEntranceException, FullDiningRoomLaneException {
        var student = event.student;

        this.getCurrentPlayerSchoolBoard().moveFromEntranceToDiningRoom(student);
    }

    /**
     * The method gets a color in input and checks if the current player should get the professor corresponding to the inputted color and if another player should lose the corresponding professor
     * @param professor indicates the color of the professor the player should get and/or remove from another player
     */
    public Map<Color, Integer> assignProfessor(Color professor) /*throws InvalidSchoolBoardIdException*/ {
        UUID parentUuid = UUID.randomUUID();
        var event = new AssignProfessorEvent(parentUuid, professor);

        this.updateSeed(event);
        var handlerReturn = this.assignProfessorHandler(event);
        this.addEvent(event);
        return handlerReturn;
    }


    public Map<Color, Integer> assignProfessorHandler(AssignProfessorEvent event) {
        var professor = event.professor;
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
     * @param student represents a student of a certain color that the player wants to move from the entrance to an archipelago
     * @param archipelagoIslandCodes represents the islandCodes of the archipelago into which the student is being moved
     */
    public void moveStudentFromEntranceToArchipelago(Color student, List<Integer> archipelagoIslandCodes) throws StudentNotInTheEntranceException {
        var parentUuid = UUID.randomUUID();

        var event = new MoveStudentFromEntranceToArchipelagoEvent(parentUuid, student, archipelagoIslandCodes);
        this.updateSeed(event);
        this.moveStudentFromEntranceToArchipelagoHandler(event);
        this.addEvent(event);
    }

    public void moveStudentFromEntranceToArchipelagoHandler(MoveStudentFromEntranceToArchipelagoEvent event) throws StudentNotInTheEntranceException {
        var student = event.student;
        var archipelagoIslandCodes = event.archipelagoIslandCodes;

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

    public void moveMotherNatureOneStepClockwiseForTesting() {
        this.motherNaturePosition = this.getNextArchipelago();
    }

    protected int getAllowedStepsNumber(){
        return this.schoolBoardIdsToCardPlayedThisRound.get(this.currentPlayerSchoolBoardId).getSteps();
    }

    /**
     * Shifts mother nature's position by the provided numberOfSteps
     * @throws InvalidNumberOfStepsException if (numberOfSteps <= 0 || numberOfSteps > maxStepsAllowed) where maxStepsAllowed depends on the card played in the round
     * @param numberOfSteps has to be a positive integer
     */
    public void moveMotherNatureNStepsClockwise(int numberOfSteps) throws InvalidNumberOfStepsException {
        var parentUuid = UUID.randomUUID();

        var event = new MoveMotherNatureNStepsClockwiseEvent(parentUuid, numberOfSteps);
        this.updateSeed(event);
        this.moveMotherNatureNStepsClockwiseHandler(event);
        this.addEvent(event);
    }

    public void moveMotherNatureNStepsClockwiseHandler(MoveMotherNatureNStepsClockwiseEvent event) throws InvalidNumberOfStepsException {
        var numberOfSteps = event.steps;

        if(numberOfSteps <= 0 || numberOfSteps > this.getAllowedStepsNumber())
            throw new InvalidNumberOfStepsException();
        for (int i = 0; i < numberOfSteps; i++)
            this.moveMotherNatureOneStepClockwise();
    }

    /**
     * The current player conquers the archipelago mother nature is currently in, placing a tower of his own color
     * and substituting any tower that was previously placed on that archipelago
     */
    public void conquerArchipelago(int schoolBoardId) {
        var parentUuid = UUID.randomUUID();

        var event = new ConquerArchipelagoEvent(parentUuid, schoolBoardId);
        this.updateSeed(event);
        this.conquerArchipelagoHandler(event);
        this.addEvent(event);
    }

    public void conquerArchipelagoHandler(ConquerArchipelagoEvent event) {
        var schoolBoardId = event.schoolBoardID;

        TowerColor playerTowerColor = this.getSchoolBoardFromSchoolBoardId(schoolBoardId).getTowerColor();

        this.motherNaturePosition.setTowerColor(playerTowerColor);
    }


    /**
     * Merges the archipelago mother nature is currently in with the archipelago on the left (one step counter-clockwise with respect to mother nature's position)
     * @return true if the archipelagos merged, false otherwise.
     */
    public boolean mergeWithPrevious() {
        var parentUuid = UUID.randomUUID();

        var event = new MergeWithPreviousEvent(parentUuid);
        this.updateSeed(event);
        var handlerReturn = this.mergeWithPreviousHandler(event);
        this.addEvent(event);
        return handlerReturn;
    }

    public boolean mergeWithPreviousHandler(MergeWithPreviousEvent event) {
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
        var parentUuid = UUID.randomUUID();

        var event = new MergeWithNextEvent(parentUuid);
        this.updateSeed(event);
        var handlerReturn = this.mergeWithNextHandler(event);
        this.addEvent(event);
        return handlerReturn;
    }

    public boolean mergeWithNextHandler(MergeWithNextEvent event) {
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
    public Map<Integer, Boolean> checkWinners(){
        Map<Integer, Boolean> schoolBoardIdToIsWinnerMap = new HashMap<>();
        List<SchoolBoard> possibleWinningSchoolBoards;


        Map<SchoolBoard, Integer> schoolBoardToTowersPlaced = new HashMap<>();

        //Initialize a map in which every schoolBoard is linked to how many towers of the corresponding color were placed
        for (SchoolBoard schoolBoard: this.schoolBoards) {
            schoolBoardToTowersPlaced.put(schoolBoard, this.getNumberOfTowersPlaced(schoolBoard.getId()));
        }

        //Find the maximum number of towers placed between all the players
        var maxTowersPlaced = schoolBoardToTowersPlaced.values()
                .stream()
                .mapToInt(Integer::intValue)
                .max();

        //Every player that placed the maximum number of towers could be a winner
        possibleWinningSchoolBoards = schoolBoardToTowersPlaced.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == maxTowersPlaced.getAsInt())
                .map(Map.Entry::getKey)
                .toList();


        if(maxTowersPlaced.isPresent() && maxTowersPlaced.getAsInt() >= this.strategy.getNumberOfTowers()){
            //If there is a winner for the number of towers placed, then prepare the winners map with it.
            for (var schoolBoard : this.schoolBoards)
                schoolBoardIdToIsWinnerMap.put(schoolBoard.getId(), possibleWinningSchoolBoards.contains(schoolBoard));

        } else if (this.onlyThreeArchipelagoLeft() || this.round.isLastRoundDone()) {
            if (possibleWinningSchoolBoards.size() > 1) {
                if (this.numberOfPlayers < GameConstants.MAX_NUMBER_OF_PLAYERS.value) {
                    // Filter by number of professors
                    var maxNumberOfProfessors = possibleWinningSchoolBoards
                            .stream()
                            .mapToInt(schoolBoard -> schoolBoard.getProfessors().size())
                            .max();

                    //possibleWinningSchoolBoards.removeIf(schoolBoard -> schoolBoard.getProfessors().size() < maxNumberOfProfessors.getAsInt());

                    possibleWinningSchoolBoards = possibleWinningSchoolBoards
                            .stream()
                            .filter(schoolBoard -> schoolBoard.getProfessors().size() == maxNumberOfProfessors.getAsInt())
                            .toList();

                } else {
                    var whiteSchoolBoards = possibleWinningSchoolBoards
                            .stream()
                            .filter(schoolBoard -> schoolBoard.getTowerColor().equals(TowerColor.WHITE));

                    var blackSchoolBoards = possibleWinningSchoolBoards
                            .stream()
                            .filter(schoolBoard -> schoolBoard.getTowerColor().equals(TowerColor.BLACK));

                    var whiteProfessors = whiteSchoolBoards
                            .mapToInt(schoolBoard -> schoolBoard.getProfessors().size())
                            .sum();

                    var blackProfessors = blackSchoolBoards
                            .mapToInt(schoolBoard -> schoolBoard.getProfessors().size())
                            .sum();

                    possibleWinningSchoolBoards = (whiteProfessors > blackProfessors ? whiteSchoolBoards : blackSchoolBoards).toList();
                }
            }
            for (var schoolBoard : this.schoolBoards)
                schoolBoardIdToIsWinnerMap.put(schoolBoard.getId(), possibleWinningSchoolBoards.contains(schoolBoard));
        }
        return schoolBoardIdToIsWinnerMap;
    }

    private boolean onlyThreeArchipelagoLeft(){
        return this.archipelagos.size() <= 3;
    }

    private int getNumberOfTowersPlaced(int schoolBoardId){
        return this.archipelagos
                .stream()
                .filter(archipelago -> archipelago.getTowerColor().equals(this.getSchoolBoardFromSchoolBoardId(schoolBoardId).getTowerColor()))
                .mapToInt(archipelagoWithThisTowerColor -> archipelagoWithThisTowerColor.getIslandCodes().size())
                .sum();
    }

    //Setters


    public void setRoundOrder(List<Integer> roundOrder) {
        var parentUuid = UUID.randomUUID();

        var event = new SetRoundOrderEvent(parentUuid, roundOrder);
        this.updateSeed(event);
        this.setRoundOrderHandler(event);
        this.addEvent(event);
    }

    public void setRoundOrderHandler(SetRoundOrderEvent event) {
        var roundOrder = event.roundOrder;

        this.round.setNewRoundOrder(roundOrder);
    }

    public void resetRoundIterator() {
        var parentUuid = UUID.randomUUID();

        var event = new ResetRoundIteratorEvent(parentUuid);
        this.updateSeed(event);
        this.resetRoundIteratorHandler(event);
        this.addEvent(event);
    }

    public void resetRoundIteratorHandler(ResetRoundIteratorEvent event) {
        this.round.resetIterator();
    }

    public void increaseRoundCount(){
        var parentUuid = UUID.randomUUID();

        var event = new IncreaseRoundCountEvent(parentUuid);
        this.updateSeed(event);
        this.increaseRoundCountHandler(event);
        this.addEvent(event);
    }

    public void increaseRoundCountHandler(IncreaseRoundCountEvent event) {
        this.round.increaseRoundCount();
    }

    public int getCurrentRound(){
        return this.round.getCurrentRound();
    }

    /**
     * This method sets the current phase of the turn
     * @throws IllegalArgumentException if(currentPhase == null)
     */
    public void setCurrentPhase(Phase currentPhase) {
        UUID parentUuid = UUID.randomUUID();

        var event = new SetCurrentPhaseEvent(parentUuid, currentPhase);

        Randomizer.setSeed(event.id.getLeastSignificantBits());

        this.setCurrentPhaseHandler(event);
        this.addEvent(event);
    }

    public void setCurrentPhaseHandler(SetCurrentPhaseEvent event) {
        var currentPhase = event.phase;

        if(currentPhase == null) throw new IllegalArgumentException();
        this.round.setCurrentPhase(currentPhase);
    }

    /**
     * @param schoolBoardId has to be a valid id of an existing schoolBoard
     */
    public void setCurrentPlayerSchoolBoardId(int schoolBoardId) {
        var parentUUID = UUID.randomUUID();
        var event = new SetCurrentPlayerSchoolBoardIDEvent(parentUUID, schoolBoardId);

        this.updateSeed(event);
        this.setCurrentPlayerSchoolBoardIdHandler(event);
        this.addEvent(event);
    }

    public void setCurrentPlayerSchoolBoardIdHandler(SetCurrentPlayerSchoolBoardIDEvent event) {
        var schoolBoardId = event.schoolBoardID;
        if(!this.getSchoolBoardIds().contains(schoolBoardId)) throw new InvalidSchoolBoardIdException("Invalid schoolboardId in input.");

        this.currentPlayerSchoolBoardId = schoolBoardId;
    }


    // Getters


    //Private getters

    /**
     * This method returns the corresponding schoolBoard to the inputted schoolBoardId
     * @param schoolBoardId has to be an existing schoolBoardId
     * @return the reference to the schoolBoard corresponding to the inputted schoolBoardId
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

    public Optional<Archipelago> getArchipelagoFromSingleIslandCode(int archipelagoIslandCode){
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

    public boolean isLastTurnInThisRound(){
        return this.round.isRoundDone();
    }

    public int getNextTurn() {
        var parentUUID = UUID.randomUUID();
        var event = new GetNextTurnEvent(parentUUID);

        this.updateSeed(event);
        var handlerReturn = this.getNextTurnHandler(event);
        this.addEvent(event);
        return handlerReturn;
    }


    public int getNextTurnHandler(GetNextTurnEvent event) { return this.round.next(); }


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
        return this.round.getCurrentPhase();
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

    public Map<Integer, Card> getSchoolBoardIdsToCardPlayedThisRound() {
        return new HashMap<>(this.schoolBoardIdsToCardPlayedThisRound);
    }

    public void resetSchoolBoardIdsToCardsPlayerThisRound(){
        var parentUUID = UUID.randomUUID();
        var event = new ResetSchoolBoardIdsToCardsPlayerThisRoundEvent(parentUUID);

        this.updateSeed(event);
        this.resetSchoolBoardIdsToCardsPlayerThisRoundHandler(event);
        this.addEvent(event);
    }

    public void resetSchoolBoardIdsToCardsPlayerThisRoundHandler(ResetSchoolBoardIdsToCardsPlayerThisRoundEvent event) {
        this.schoolBoardIdsToCardPlayedThisRound.clear();
    }

    public int getCurrentPlayerSchoolBoardId() {
        return this.currentPlayerSchoolBoardId;
    }



    public void setActionPhaseSubTurn(ActionPhaseSubTurn actionPhaseSubTurn) {
        var parentUUID = UUID.randomUUID();
        var event = new SetActionPhaseSubTurnEvent(parentUUID, actionPhaseSubTurn);

        this.updateSeed(event);
        this.setActionPhaseSubTurnHandler(event);
        this.addEvent(event);
    }

    public void setActionPhaseSubTurnHandler(SetActionPhaseSubTurnEvent event) {
        var actionPhaseSubTurn = event.actionPhaseSubTurn;
        this.round.setActionPhaseSubTurn(actionPhaseSubTurn);
    }

    public ActionPhaseSubTurn getActionPhaseSubTurn() {
        return this.round.getActionPhaseSubTurn();
    }

    //Created for testing - could be useful or dangerous

    public void setMotherNaturePositionForTesting(Archipelago motherNaturePosition) {
        this.motherNaturePosition = motherNaturePosition;
    }

    public Optional<TowerColor> getTowerColorFromSchoolBoardID(Integer schoolBoardID) {
        return this.schoolBoards.stream()
                .filter(s -> s.getId() == schoolBoardID)
                .map(SchoolBoard::getTowerColor)
                .findFirst();
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
        return this.schoolBoardIdsToCardPlayedThisRound;
    }

    public void setLastRoundTrue(){
        this.round.setIsLastRoundTrue();
    }

    public boolean isLastRound(){
        return this.round.isLastRound();
    }

    public LightGameState lightify(){
        return new LightGameState(
                this.archipelagos.stream().map(Archipelago::lightify).toList(),
                this.schoolBoards.stream().map(SchoolBoard::lightify).toList(),
                this.clouds,
                this.currentPlayerSchoolBoardId,
                this.round.getCurrentPhase(),
                this.round.getRoundOrder(),
                this.archipelagos.indexOf(motherNaturePosition),
                this.schoolBoardIdsToLastCardPlayed,
                null,
                null
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
