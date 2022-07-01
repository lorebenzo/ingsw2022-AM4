package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.MoveNotAvailableException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.event_sourcing.Aggregate;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.ExpertNumberOfPlayersStrategyFactory;
import it.polimi.ingsw.server.repository.exceptions.DBQueryException;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class ExpertGameState extends GameState {

    private final List<PlayableCharacter> availableCharacters;
    protected PlayableCharacter characterPlayedInCurrentTurn;


    /**
     * @param playersNumber number of players in the game, must be between 2 (inclusive) and 4 (inclusive)
     * @throws IllegalArgumentException                if the argument representing the number of players is not between 2 and 4
     * @throws GameStateInitializationFailureException if there is a problem in the initialization of the archipelagos
     *                                                 or the schoolBoards,
     *                                                 or the clouds.
     */
    public ExpertGameState(int playersNumber) throws GameStateInitializationFailureException {
        super(playersNumber);
        this.availableCharacters = new ArrayList<>();

        try {
            this.extractCharacters();
        } catch(EmptyStudentSupplyException ignored) { throw new GameStateInitializationFailureException(); }

        this.characterPlayedInCurrentTurn = new PlayableCharacter(Character.NONE);
    }

    /**
     *ONLY FOR TESTING
     */
    protected ExpertGameState(int playersNumber, List<PlayableCharacter> availableCharacters) throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        super(playersNumber);
        this.availableCharacters = availableCharacters;

        for (PlayableCharacter playableCharacter: this.availableCharacters) {
            this.refillGivenCharacter(playableCharacter);
        }

        this.characterPlayedInCurrentTurn = new PlayableCharacter(Character.NONE);
    }

    /**
     * This method chooses the appropriate strategy according to the number of players
     */
    @Override
    protected void chooseStrategy() {
        this.strategy = ExpertNumberOfPlayersStrategyFactory.getCorrectStrategy(numberOfPlayers);
    }

    /**
     * This method created an archipelago for the expert mode
     * @param code is the starting islandCode of the archipelago
     * @return the newly created archipelago
     */
    @Override
    protected ExpertArchipelago createArchipelago(int code) {
        return new ExpertArchipelago(code);
    }

    /**
     * This method initializes the schoolBoards according to the appropriate strategy depending on the number of players.
     *
     * @return a List of SchoolBoard containing the already initialized schoolBoards, students in the entrance included.
     * @throws EmptyStudentSupplyException if the studentSupply representing the bag is empty and cannot fulfill the initialization process
     */
    @Override
    protected List<SchoolBoard> initializeSchoolBoards() throws EmptyStudentSupplyException {
        return this.strategy.initializeSchoolBoards(this.studentFactory);
    }

    /**
     * This method randomly extracts 3 different characters with which the game will be played.
     */
    private void extractCharacters() throws EmptyStudentSupplyException {
        List<Character> characters = new ArrayList<>(List.of(Character.values()));
        // removes NONE
        characters.remove(0);
        Collections.shuffle(characters);

        for (int i = 0; i < 3; i++){
            this.availableCharacters.add(PlayableCharacter.createCharacter(characters.get(i)));
            this.refillGivenCharacter(this.availableCharacters.get(i));
        }
    }

    /**
     * This method refills the PlayableCharactersWithStudents that may have been used during a turn
     * @param playableCharacter is the PlayableCharacter to be refilled
     * @throws EmptyStudentSupplyException if the student supply is empty and the refill couldn't be completed
     */
    private void refillGivenCharacter(PlayableCharacter playableCharacter) throws EmptyStudentSupplyException {
        while (playableCharacter.getStudents() != null && playableCharacter.getStudents().size() < playableCharacter.getInitialStudentsNumberOnCharacter())
            playableCharacter.addStudent(this.studentFactory.getStudent());
    }

    /**
     * This method refills the PlayableCHaracter that was played during the turn
     * @throws EmptyStudentSupplyException if the student supply is empty and the refill couldn't be completed
     */
    public void refillCharacter() throws EmptyStudentSupplyException {
        this.refillGivenCharacter(this.characterPlayedInCurrentTurn);
    }


    /**
     * This method executes the effect of the character with ID 1. It takes one student from the character and puts it on the inputted archipelago
     * @param color represents the student to be moved
     * @param archipelagoIslandCode represent the archipelago onto which the inputted student will be moved
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws InvalidArchipelagoIdException if the inputted archipelago code is invalid
     * @throws StudentNotOnCharacterException if the inputted student is not actually present on character
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playPutOneStudentFromCharacterToArchipelago(Color color, int archipelagoIslandCode) throws MoveNotAvailableException, InvalidArchipelagoIdException, StudentNotOnCharacterException, NotEnoughCoinsException {
        if(color == null) throw new IllegalArgumentException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.PUT_ONE_STUDENT_FROM_CHARACTER_TO_ARCHIPELAGO.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        Optional<Archipelago> selectedArchipelago = this.getArchipelagoFromSingleIslandCode(archipelagoIslandCode);
        if(selectedArchipelago.isEmpty()) throw new InvalidArchipelagoIdException();

        if(!selectedCharacter.get().getStudents().contains(color)) throw new StudentNotOnCharacterException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        selectedCharacter.get().removeStudent(color);
        selectedArchipelago.get().addStudent(color);

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();

    }

    /**
     * This method executes the effect of the character with ID 2. It allows the player to take control of the a professor even if he has the same number of
     * students on the corresponding diningRoom table
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playGetProfessorsWithSameStudents() throws MoveNotAvailableException, NotEnoughCoinsException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.GET_PROFESSORS_WITH_SAME_STUDENTS.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();

        this.assignProfessorsWithEffect();
    }

    @Override
    protected boolean compareCurrentPlayersStudentsNumberWithOthersMax(int currentPlayerNumberOfStudentsInDiningRoomLane, int otherSchoolBoardsMaxStudentsInDiningRoomLane) {
        if(this.characterPlayedInCurrentTurn != null && this.characterPlayedInCurrentTurn.getCharacterId() == Character.GET_PROFESSORS_WITH_SAME_STUDENTS.characterId)
            return currentPlayerNumberOfStudentsInDiningRoomLane >= otherSchoolBoardsMaxStudentsInDiningRoomLane && currentPlayerNumberOfStudentsInDiningRoomLane > 0;
        return super.compareCurrentPlayersStudentsNumberWithOthersMax(currentPlayerNumberOfStudentsInDiningRoomLane,otherSchoolBoardsMaxStudentsInDiningRoomLane);
    }

    /**
     * This method assigns the professors after the activation of the effect of the character with ID 2
     */
    protected void assignProfessorsWithEffect(){
        for (Color professor: Color.values()) {
            Integer previousOwnerSchoolBoardId = this.assignProfessor(professor).get(professor);
            if(previousOwnerSchoolBoardId != null)
                this.characterPlayedInCurrentTurn.putProfessor(professor, previousOwnerSchoolBoardId);
        }
    }


    /**
     * This method resets the ownership (with the normal rules) of every professor after the effect of the character is finished
     */
    @Override
    public void assignProfessorsAfterEffect() {
        Integer professorToOriginalOwnerSchoolBoardId;

        if(this.characterPlayedInCurrentTurn.getProfessorToOriginalOwnerMap() != null){
            for (Color color : this.characterPlayedInCurrentTurn.getProfessorToOriginalOwnerMap().keySet()) {
                professorToOriginalOwnerSchoolBoardId = this.characterPlayedInCurrentTurn.getProfessorToOriginalOwnerMap().get(color);
                SchoolBoard originalProfessorOwnerSchoolBoard = this.getSchoolBoardFromSchoolBoardId(professorToOriginalOwnerSchoolBoardId);

                if(this.getCurrentPlayerSchoolBoard().getDiningRoomLaneColorToNumberOfStudents().get(color).intValue() == originalProfessorOwnerSchoolBoard.getDiningRoomLaneColorToNumberOfStudents().get(color).intValue()){
                    this.getCurrentPlayerSchoolBoard().removeProfessor(color);
                    originalProfessorOwnerSchoolBoard.addProfessor(color);
                }


            }
            this.characterPlayedInCurrentTurn.clearProfessorsToOriginalOwnerMap();
        }
    }

    /**
     * This method executes the effect of the character with ID 3. It allows the player to move mother nature to any archipelago.
     * @param archipelagoIslandCode represent an archipelago through one of its codes
     * @return true if the archipelagos merged as a result, false otherwise
     * @throws InvalidArchipelagoIdException if the inputted archipelago code is invalid
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public boolean playMoveMotherNatureToAnyArchipelago(int archipelagoIslandCode) throws InvalidArchipelagoIdException, MoveNotAvailableException, NotEnoughCoinsException {
        boolean mergePreviousPerformed = false;
        boolean mergeNextPerformed = false;

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.MOVE_MOTHER_NATURE_TO_ANY_ARCHIPELAGO.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        Optional<Archipelago> selectedArchipelago = this.getArchipelagoFromSingleIslandCode(archipelagoIslandCode);
        if(selectedArchipelago.isEmpty()) throw new InvalidArchipelagoIdException();

        Archipelago originalMotherNaturePositionIslandCode = this.motherNaturePosition;
        this.motherNaturePosition = selectedArchipelago.get();

        if(!this.motherNaturePosition.isLocked()){
            Optional<Integer> mostInfluentSchoolBoardId = this.getMostInfluentSchoolBoardId(this.motherNaturePosition.getIslandCodes());
            if(mostInfluentSchoolBoardId.isPresent()){
                this.conquerArchipelago(mostInfluentSchoolBoardId.get());
                mergePreviousPerformed = this.mergeWithPrevious();
                mergeNextPerformed = this.mergeWithNext();
            }
        }
        else
            this.unlockMotherNaturePosition();

        this.motherNaturePosition = originalMotherNaturePositionIslandCode;


        this.characterPlayedInCurrentTurn = selectedCharacter.get();
        this.payCharacter();

        return mergePreviousPerformed || mergeNextPerformed;
    }

    /**
     * This method executes the effect of the character with ID 4. It allows the player to move mother nature for 2 additional steps.
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playTwoAdditionalSteps() throws MoveNotAvailableException, NotEnoughCoinsException {

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.TWO_ADDITIONAL_STEPS.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();
        this.payCharacter();
    }

    private int getAdditionalSteps(){
        if(this.characterPlayedInCurrentTurn.getCharacterId() == Character.TWO_ADDITIONAL_STEPS.characterId)
            return 2;
        else
            return 0;
    }

    @Override
    protected int getAllowedStepsNumber() {
        return super.getAllowedStepsNumber() + this.getAdditionalSteps();
    }


    /**
     *
     * @param archipelagoToLock is the archipelago that will be locked
     * @throws ArchipelagoAlreadyLockedException if the inputted Archipelago is already locked
     */
    private void lockArchipelago(Archipelago archipelagoToLock) throws ArchipelagoAlreadyLockedException {
        if(archipelagoToLock == null) throw new IllegalArgumentException();

        archipelagoToLock.lock();
    }

    /**
     * This method executes the effect of the character with ID 5. It allows the player to lock an archipelago of choice.
     * @param archipelagoIslandCode represent an archipelago through one of its codes
     * @throws NoAvailableLockException if there are no locks available on the character
     * @throws InvalidArchipelagoIdException if the inputted archipelago code is invalid
     * @throws ArchipelagoAlreadyLockedException if the inputted Archipelago is already locked
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playCharacterLock(int archipelagoIslandCode) throws NoAvailableLockException, InvalidArchipelagoIdException, ArchipelagoAlreadyLockedException, MoveNotAvailableException, NotEnoughCoinsException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.LOCK_ARCHIPELAGO.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        if(!selectedCharacter.get().isLockAvailable()) throw new NoAvailableLockException();

        Optional<Archipelago> selectedArchipelago = this.getArchipelagoFromSingleIslandCode(archipelagoIslandCode);
        if(selectedArchipelago.isEmpty()) throw new InvalidArchipelagoIdException();

        this.lockArchipelago(selectedArchipelago.get());
        selectedCharacter.get().useLock();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    /**
     * This method unlocks the archipelago onto which mother nature terminates its movement
     */
    @Override
    public void unlockMotherNaturePosition(){
        var tmp = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.LOCK_ARCHIPELAGO.characterId).findFirst();
        if(this.motherNaturePosition.isLocked() && tmp.isPresent()){
            this.motherNaturePosition.unlock();
            tmp.get().unLock();
        }
    }

    /**
     * @param doTowersCount is true if the towers will have to count during the influence calculation, false otherwise
     */
    @Override
    public void setTowersInfluenceForAllArchipelagos(boolean doTowersCount){
        for (Archipelago archipelago: this.archipelagos) {
            archipelago.setTowersInfluence(doTowersCount);
        }
    }

    /**
     * This method executes the effect of the character with ID 6. It sets the influence given by the towers on every archipelago to zero.
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playTowersDontCount() throws MoveNotAvailableException, NotEnoughCoinsException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.TOWERS_DONT_COUNT.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.setTowersInfluenceForAllArchipelagos(false);

        this.payCharacter();
    }

    /**
     * This method executes the effect of the character with ID 7. It allows the player to swap up to three students between its schoolBoard's entrance
     * and the character
     * @param studentsFromCharacter is a list representing the students to get from the character
     * @param studentsFromEntrance is a list representing the students to give from the entrance
     * @throws InvalidStudentListsLengthException if the two inputted lists have different lengths
     * @throws StudentNotOnCharacterException if one of the elements of studentsFromCharacter is not actually present on the character
     * @throws StudentNotInTheEntranceException if one of the elements of studentsFromCharacter is not actually present in the entrance
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playSwapThreeStudentsBetweenCharacterAndEntrance(List<Color> studentsFromCharacter, List<Color> studentsFromEntrance) throws InvalidStudentListsLengthException, MoveNotAvailableException, StudentNotOnCharacterException, StudentNotInTheEntranceException, NotEnoughCoinsException {
        if(studentsFromCharacter == null || studentsFromEntrance == null || studentsFromCharacter.contains(null) || studentsFromEntrance.contains(null)) throw new IllegalArgumentException();

        if(studentsFromCharacter.size() != studentsFromEntrance.size() || studentsFromEntrance.size() > 3) throw new InvalidStudentListsLengthException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.SWAP_THREE_STUDENTS_BETWEEN_CHARACTER_AND_ENTRANCE.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        if(!selectedCharacter.get().containsAllStudents(studentsFromCharacter)) throw new StudentNotOnCharacterException();
        if(!this.getCurrentPlayerSchoolBoard().containsAllStudentsInTheEntrance(studentsFromEntrance)) throw new StudentNotInTheEntranceException();

        for (Color student: studentsFromCharacter ) {
            selectedCharacter.get().removeStudent(student);
            this.getCurrentPlayerSchoolBoard().addStudentToEntrance(student);
        }

        for (Color student: studentsFromEntrance ) {
            this.getCurrentPlayerSchoolBoard().removeStudentFromEntrance(student);
            selectedCharacter.get().addStudent(student);
        }

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    /**
     * This method executes the effect of the character with ID 8. It allows the player to get 2 additional units of influence during the influence calculation.
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playTwoAdditionalInfluence() throws MoveNotAvailableException, NotEnoughCoinsException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.TWO_ADDITIONAL_INFLUENCE.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();

    }

    /**
     * This method gets an archipelago in input and returns a map where every entry links a schoolBoard with its influence on the inputted archipelago
     * @param archipelagoIslandCodes is a List of Integer uniquely identifying an archipelago
     * @return a Map Integer, Integer where the key is the schoolBoardId and the value is the influence on the inputted archipelago
     */
    @Override
    public Optional<Map<Integer, Integer>> getInfluence(List<Integer> archipelagoIslandCodes) {
        Optional<Map<Integer, Integer>> schoolBoardToInfluenceMap = super.getInfluence(archipelagoIslandCodes);
        if(this.characterPlayedInCurrentTurn.getCharacterId() == Character.TWO_ADDITIONAL_INFLUENCE.characterId){
            schoolBoardToInfluenceMap.ifPresent(schoolBoardToInfluence -> schoolBoardToInfluence.put(this.getCurrentPlayerSchoolBoardId(), schoolBoardToInfluence.get(this.getCurrentPlayerSchoolBoardId()) + 2));
        }
        return schoolBoardToInfluenceMap;
    }


    private void setColorThatDoesntCountForAllArchipelagos(Color color){
        for (Archipelago archipelago: this.archipelagos) {
            archipelago.setColorThatDoesntCount(color);
        }
    }

    /**
     * This method executes the effect of the character with ID 9. It allows the player to choose a color that won't count during the influence calculation.
     * @param color is the color that won't count during the influence calculation
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playColorDoesntCount(Color color) throws MoveNotAvailableException, NotEnoughCoinsException {
        if(color == null) throw new IllegalArgumentException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.COLOR_DOESNT_COUNT.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        this.setColorThatDoesntCountForAllArchipelagos(color);

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    @Override
    public void resetColorThatDoesntCountForAllArchipelagos(){
        this.setColorThatDoesntCountForAllArchipelagos(null);
    }

    /**
     * This method executes the effect of the character with ID 10. It allows the player to swap up to two students between its entrance and diningRoom.
     * @param studentFromEntrance is the list of students to get from the entrance
     * @param studentsFromDiningRoom is the list of students to get from the diningRoom
     * @throws InvalidStudentListsLengthException if the two inputted lists have different lengths
     * @throws StudentNotInTheEntranceException if one of the elements of studentsFromCharacter is not actually present in the entrance
     * @throws FullDiningRoomLaneException if the diningRoom table corresponding to one of the colors is full
     * @throws StudentsNotInTheDiningRoomException if the one of the elements of studentsFromEntrance is not actually present in the entrance
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playSwapTwoStudentsBetweenEntranceAndDiningRoom(List<Color> studentFromEntrance, List<Color> studentsFromDiningRoom) throws InvalidStudentListsLengthException, MoveNotAvailableException, StudentNotInTheEntranceException, FullDiningRoomLaneException, StudentsNotInTheDiningRoomException, NotEnoughCoinsException {
        if(studentsFromDiningRoom == null || studentFromEntrance == null || studentsFromDiningRoom.contains(null) || studentFromEntrance.contains(null)) throw new IllegalArgumentException();

        if(studentsFromDiningRoom.size() != studentFromEntrance.size() || studentFromEntrance.size() > 3) throw new InvalidStudentListsLengthException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.SWAP_TWO_STUDENTS_BETWEEN_ENTRANCE_AND_DINING_ROOM.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        if(!this.getCurrentPlayerSchoolBoard().containsAllStudentsInTheDiningRoom(studentsFromDiningRoom)) throw new StudentsNotInTheDiningRoomException();
        if(!this.getCurrentPlayerSchoolBoard().containsAllStudentsInTheEntrance(studentFromEntrance)) throw new StudentNotInTheEntranceException();

        for (Color student: studentsFromDiningRoom ) {
            this.getCurrentPlayerSchoolBoard().removeStudentFromDiningRoom(student);
            this.getCurrentPlayerSchoolBoard().addStudentToEntrance(student);
        }

        for (Color student: studentFromEntrance ) {
            this.getCurrentPlayerSchoolBoard().removeStudentFromEntrance(student);
            this.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(student);
        }

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    //11
    private void putOneStudentFromCharacterToDiningRoom(PlayableCharacter selectedCharacter, Color student) throws StudentNotOnCharacterException, FullDiningRoomLaneException {
        if(!selectedCharacter.removeStudent(student)) throw new StudentNotOnCharacterException();

        this.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(student);
    }

    /**
     * This method executes the effect of the character with ID 11. It allows the player to get one student from the character and put it into his diningRoom
     * @param color represents the student to be moved from the character to the diningRoom
     * @throws StudentNotOnCharacterException if one of the elements of studentsFromCharacter is not actually present on the character
     * @throws FullDiningRoomLaneException if the diningRoom table corresponding to one of the colors is full
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void playPutOneStudentFromCharacterToDiningRoom(Color color) throws StudentNotOnCharacterException, FullDiningRoomLaneException, MoveNotAvailableException, NotEnoughCoinsException {
        if(color == null) throw new IllegalArgumentException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.PUT_ONE_STUDENT_FROM_CHARACTER_INTO_DINING_ROOM.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        this.putOneStudentFromCharacterToDiningRoom(selectedCharacter.get(), color);

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    //12
    private void putThreeStudentsInTheBag(Color color) throws StudentsNotInTheDiningRoomException {
        for (SchoolBoard schoolBoard: this.schoolBoards ) {
            for (int i = 0; i < 3; i++) {
                if(schoolBoard.getDiningRoomLaneColorToNumberOfStudents().get(color) >= 1){
                    schoolBoard.removeStudentFromDiningRoom(color);
                    this.studentFactory.studentSupply.put(color, this.studentFactory.studentSupply.get(color) + 1 );
                }
            }
        }
    }

    /**
     * This method executes the effect of the character with ID 12. It makes every player to get three students of the inputted color from the diningRoom
     * and put them into the bag
     * @param color is the color of the students that have to be removed from the diningRoom
     * @throws StudentsNotInTheDiningRoomException if the one of the elements of studentsFromEntrance is not actually present in the entrance
     * @throws MoveNotAvailableException if the move is not available in the game
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character

     */
    @Override
    public void playPutThreeStudentsInTheBag(Color color) throws MoveNotAvailableException, NotEnoughCoinsException, StudentsNotInTheDiningRoomException {
        if(color == null) throw new IllegalArgumentException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.PUT_THREE_STUDENTS_IN_THE_BAG.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        this.putThreeStudentsInTheBag(color);

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    @Override
    public List<PlayableCharacter> getAvailableCharacters(){
        return new ArrayList<>(this.availableCharacters);
    }

    @Override
    public boolean wasCharacterPlayedInCurrentTurn() {
        return this.characterPlayedInCurrentTurn.getCharacterId() != Character.NONE.characterId;
    }

    @Override
    public void resetCharacterPlayedThisTurn(){
        this.characterPlayedInCurrentTurn = new PlayableCharacter(Character.NONE);
    }

    private void payCharacter() {
        if(this.characterPlayedInCurrentTurn.getCharacterId() != Character.NONE.characterId){
            this.getCurrentPlayerSchoolBoard().payCharacter(this.characterPlayedInCurrentTurn.getCurrentCost());
            this.characterPlayedInCurrentTurn.increaseCost();
        }
    }

    /**
     * This method returns the light version of the GameState, with all the useful information to be sent over the network
     * @return a LightGameState representing the light version of the GameState, with all the useful information to be sent over the network
     */
    @Override
    public LightGameState lightify() {
        return new LightGameState(
                super.archipelagos.stream().map(Archipelago::lightify).toList(),
                super.schoolBoards.stream().map(SchoolBoard::lightify).toList(),
                super.clouds,
                super.currentPlayerSchoolBoardId,
                super.round.getCurrentPhase(),
                super.round.getRoundOrder(),
                super.archipelagos.indexOf(motherNaturePosition),
                super.schoolBoardIdsToLastCardPlayed,

                this.availableCharacters.stream().map(PlayableCharacter::lightify).toList(),
                this.characterPlayedInCurrentTurn != null ? this.characterPlayedInCurrentTurn.lightify() : null
        );
    }

    @Override
    public void createSnapshot() {}

    @Override
    public Aggregate rollback() {
        return this;
    }
}
