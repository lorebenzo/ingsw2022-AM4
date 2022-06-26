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
    public ExpertGameState(int playersNumber, List<PlayableCharacter> availableCharacters) throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        super(playersNumber);
        this.availableCharacters = availableCharacters;

        for (PlayableCharacter playableCharacter: this.availableCharacters) {
            this.refillGivenCharacter(playableCharacter);
        }

        this.characterPlayedInCurrentTurn = new PlayableCharacter(Character.NONE);
    }

    @Override
    protected void chooseStrategy() {
        this.strategy = ExpertNumberOfPlayersStrategyFactory.getCorrectStrategy(numberOfPlayers);
    }

    @Override
    protected ExpertArchipelago createArchipelago(int code) {
        return new ExpertArchipelago(code);
    }

    /**
     * This method initializes the schoolBoards according to the appropriate strategy depending on the number of players.
     *
     * @return a List<SchoolBoard> containing the already initialized schoolBoards, students in the entrance included.
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

    private void refillGivenCharacter(PlayableCharacter playableCharacter) throws EmptyStudentSupplyException {
        while (playableCharacter.getStudents() != null && playableCharacter.getStudents().size() < playableCharacter.getInitialStudentsNumberOnCharacter())
            playableCharacter.addStudent(this.studentFactory.getStudent());
    }

    public void refillCharacter() throws EmptyStudentSupplyException {
        this.refillGivenCharacter(this.characterPlayedInCurrentTurn);
    }

    //RULES MODIFIERS

    //1 OK AND TESTED
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

    //2 OK AND TESTED
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

    protected void assignProfessorsWithEffect(){
        for (Color professor: Color.values()) {
            Integer previousOwnerSchoolBoardId = this.assignProfessor(professor).get(professor);
            if(previousOwnerSchoolBoardId != null)
                this.characterPlayedInCurrentTurn.putProfessor(professor, previousOwnerSchoolBoardId);
        }
    }

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

    //3 OK AND TESTED
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

    //4 OK AND TESTED
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


    //5 OK AND TESTED
    private void lockArchipelago(Archipelago archipelagoToLock) throws ArchipelagoAlreadyLockedException {
        if(archipelagoToLock == null) throw new IllegalArgumentException();

        archipelagoToLock.lock();
    }

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

    @Override
    public void unlockMotherNaturePosition(){
        var tmp = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.LOCK_ARCHIPELAGO.characterId).findFirst();
        if(this.motherNaturePosition.isLocked() && tmp.isPresent()){
            this.motherNaturePosition.unlock();
            tmp.get().unLock();
        }
    }

    //6 OK AND TESTED
    @Override
    public void setTowersInfluenceForAllArchipelagos(boolean doTowersCount){
        for (Archipelago archipelago: this.archipelagos) {
            archipelago.setTowersInfluence(doTowersCount);
        }
    }
    @Override
    public void playTowersDontCount() throws MoveNotAvailableException, NotEnoughCoinsException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.TOWERS_DONT_COUNT.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.setTowersInfluenceForAllArchipelagos(false);

        this.payCharacter();
    }

    //7 OK AND TESTED
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

    //8 OK AND TESTED
    @Override
    public void playTwoAdditionalInfluence() throws MoveNotAvailableException, NotEnoughCoinsException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.TWO_ADDITIONAL_INFLUENCE.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(this.getCurrentPlayerSchoolBoard().getCoins() < selectedCharacter.get().getCurrentCost()) throw new NotEnoughCoinsException();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();

    }

    @Override
    public Optional<Map<Integer, Integer>> getInfluence(List<Integer> archipelagoIslandCodes) {
        Optional<Map<Integer, Integer>> schoolBoardToInfluenceMap = super.getInfluence(archipelagoIslandCodes);
        if(this.characterPlayedInCurrentTurn.getCharacterId() == Character.TWO_ADDITIONAL_INFLUENCE.characterId){
            schoolBoardToInfluenceMap.ifPresent(schoolBoardToInfluence -> schoolBoardToInfluence.put(this.getCurrentPlayerSchoolBoardId(), schoolBoardToInfluence.get(this.getCurrentPlayerSchoolBoardId()) + 2));
        }
        return schoolBoardToInfluenceMap;
    }

    //9 OK AND TESTED
    private void setColorThatDoesntCountForAllArchipelagos(Color color){
        for (Archipelago archipelago: this.archipelagos) {
            archipelago.setColorThatDoesntCount(color);
        }
    }
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

    //10 OK AND TESTED
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

    //11 OK AND TESTED
    private void putOneStudentFromCharacterToDiningRoom(PlayableCharacter selectedCharacter, Color student) throws StudentNotOnCharacterException, FullDiningRoomLaneException {
        if(!selectedCharacter.removeStudent(student)) throw new StudentNotOnCharacterException();

        this.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(student);
    }
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

    //12 OK AND TESTED
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
