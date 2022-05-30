package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidNumberOfStepsException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.MoveNotAvailableException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.*;

public class ExpertGameState extends GameState {

    private final List<PlayableCharacter> availableCharacters;
    private PlayableCharacter characterPlayedInCurrentTurn;


    /**
     * @param playersNumber number of players in the game, must be between 2 (inclusive) and 4 (inclusive)
     * @throws IllegalArgumentException                if the argument representing the number of players is not between 2 and 4
     * @throws GameStateInitializationFailureException if there is a problem in the initialization of the archipelagos
     *                                                 or the schoolBoards,
     *                                                 or the clouds.
     */
    public ExpertGameState(int playersNumber) throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        super(playersNumber);
        this.availableCharacters = new ArrayList<>();

        this.extractCharacters();

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

        this.characterPlayedInCurrentTurn = null;
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
        Collections.shuffle(characters);


        for (int i = 0; i < 3; i++){
            this.availableCharacters.add(PlayableCharacter.initializeCharacter(characters.get(i)));
            this.refillGivenCharacter(this.availableCharacters.get(i));
        }
    }

    private void refillGivenCharacter(PlayableCharacter playableCharacter) throws EmptyStudentSupplyException {
        while (playableCharacter.getStudents().size() < playableCharacter.getInitialStudentsNumberOnCharacter())
            playableCharacter.addStudent(this.studentFactory.getStudent());
    }

    public void refillCharacter() throws EmptyStudentSupplyException {
        this.refillGivenCharacter(this.characterPlayedInCurrentTurn);
    }

    //RULES MODIFIERS

    //1 OK
    @Override
    public void playPutOneStudentFromCharacterToArchipelago(Color color, int archipelagoIslandCode) throws MoveNotAvailableException, InvalidArchipelagoIdException, StudentNotOnCharacterException {
        if(color == null) throw new IllegalArgumentException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.PUT_ONE_STUDENT_FROM_CHARACTER_TO_ARCHIPELAGO.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(!selectedCharacter.get().getStudents().contains(color)) throw new StudentNotOnCharacterException();

        Optional<Archipelago> selectedArchipelago = this.getArchipelagoFromSingleIslandCode(archipelagoIslandCode);
        if(selectedArchipelago.isEmpty()) throw new InvalidArchipelagoIdException();

        selectedCharacter.get().removeStudent(color);
        selectedArchipelago.get().addStudent(color);

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();

    }

    //2 OK
    @Override
    public void playGetProfessorsWithSameStudents() throws MoveNotAvailableException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.GET_PROFESSORS_WITH_SAME_STUDENTS.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    @Override
    protected boolean compareCurrentPlayersStudentsNumberWithOthersMax(int currentPlayerNumberOfStudentsInDiningRoomLane, int otherSchoolBoardsMaxStudentsInDiningRoomLane) {
        if(this.characterPlayedInCurrentTurn.getCharacterId() == Character.GET_PROFESSORS_WITH_SAME_STUDENTS.characterId)
            return currentPlayerNumberOfStudentsInDiningRoomLane >= otherSchoolBoardsMaxStudentsInDiningRoomLane;
        return super.compareCurrentPlayersStudentsNumberWithOthersMax(currentPlayerNumberOfStudentsInDiningRoomLane,otherSchoolBoardsMaxStudentsInDiningRoomLane);
    }

    @Override
    public void resetProfessors(){
        for (Color professor: Color.values()) {
            this.assignProfessor(professor);
        }
    }

    //3 OK
    @Override
    public boolean playMoveMotherNatureToAnyArchipelago(int archipelagoIslandCode) throws InvalidArchipelagoIdException, MoveNotAvailableException {
        boolean mergePreviousPerformed = false;
        boolean mergeNextPerformed = false;

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.MOVE_MOTHER_NATURE_TO_ANY_ARCHIPELAGO.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        Optional<Archipelago> selectedArchipelago = this.getArchipelagoFromSingleIslandCode(archipelagoIslandCode);
        if(selectedArchipelago.isEmpty()) throw new InvalidArchipelagoIdException();

        Archipelago originalMotherNaturePositionIslandCode = this.motherNaturePosition;


        if(this.getMostInfluentSchoolBoardId(selectedArchipelago.get().getIslandCodes()).isPresent()){
            this.conquerArchipelago(this.getMostInfluentSchoolBoardId(selectedArchipelago.get().getIslandCodes()).get());
            mergePreviousPerformed = this.mergeWithPrevious();
            mergeNextPerformed = this.mergeWithNext();
        }

        this.unlockMotherNaturePosition();
        this.motherNaturePosition = originalMotherNaturePositionIslandCode;


        this.characterPlayedInCurrentTurn = selectedCharacter.get();
        this.payCharacter();

        return mergePreviousPerformed || mergeNextPerformed;
    }

    //4 OK
    @Override
    public void playTwoAdditionalSteps() throws MoveNotAvailableException {

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.TWO_ADDITIONAL_STEPS.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

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


    //5 OK
    private void lockArchipelago(Archipelago archipelagoToLock) throws ArchipelagoAlreadyLockedException {
        if(archipelagoToLock == null) throw new IllegalArgumentException();

        archipelagoToLock.lock();
    }

    @Override
    public void playCharacterLock(int archipelagoIslandCode) throws NoAvailableLockException, InvalidArchipelagoIdException, ArchipelagoAlreadyLockedException, MoveNotAvailableException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.LOCK_ARCHIPELAGO.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

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
        if(this.motherNaturePosition.isLocked()){
            this.motherNaturePosition.unlock();
            var tmp = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.LOCK_ARCHIPELAGO.characterId).findFirst();
            tmp.ifPresent(Playable::unLock);
        }
    }

    //6 OK
    @Override
    public void setTowersInfluenceForAllArchipelagos(boolean doTowersCount){
        for (Archipelago archipelago: this.archipelagos) {
            archipelago.setTowersInfluence(doTowersCount);
        }
    }
    @Override
    public void playTowersDontCount() throws MoveNotAvailableException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.TOWERS_DONT_COUNT.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.setTowersInfluenceForAllArchipelagos(false);

        this.payCharacter();
    }

    //7 OK
    @Override
    public void playSwapThreeStudentsBetweenCharacterAndEntrance(List<Color> students1, List<Color> students2) throws InvalidStudentListsLengthException, MoveNotAvailableException, StudentNotOnCharacterException, StudentNotInTheEntranceException {
        if(students1 == null || students2 == null || students1.contains(null) || students2.contains(null)) throw new IllegalArgumentException();

        if(students1.size() != students2.size() || students2.size() > 3) throw new InvalidStudentListsLengthException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.SWAP_THREE_STUDENTS_BETWEEN_CHARACTER_AND_ENTRANCE.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(!selectedCharacter.get().containsAllStudents(students1)) throw new StudentNotOnCharacterException();
        if(!this.getCurrentPlayerSchoolBoard().containsAllStudentsInTheEntrance(students2)) throw new StudentNotInTheEntranceException();

        for (Color student: students1 ) {
            selectedCharacter.get().removeStudent(student);
            this.getCurrentPlayerSchoolBoard().addStudentToEntrance(student);
        }

        for (Color student: students2 ) {
            this.getCurrentPlayerSchoolBoard().removeStudentFromEntrance(student);
            selectedCharacter.get().addStudent(student);
        }

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    //8 OK
    @Override
    public void playTwoAdditionalInfluence() throws MoveNotAvailableException {
        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.TWO_ADDITIONAL_INFLUENCE.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();

    }

    @Override
    public Optional<Map<Integer, Integer>> getInfluence(List<Integer> archipelagoIslandCodes) {
        Optional<Map<Integer, Integer>> schoolBoardToInfluenceMap = Optional.empty();
        if(this.characterPlayedInCurrentTurn.getCharacterId() == Character.TWO_ADDITIONAL_INFLUENCE.characterId){
            schoolBoardToInfluenceMap = super.getInfluence(archipelagoIslandCodes);
            schoolBoardToInfluenceMap.ifPresent(schoolBoardToInfluence -> schoolBoardToInfluence.put(this.getCurrentPlayerSchoolBoardId(), schoolBoardToInfluence.get(this.getCurrentPlayerSchoolBoardId()) + 2));
        }
        return schoolBoardToInfluenceMap;
    }

    //9 OK
    private void setColorThatDoesntCountForAllArchipelagos(Color color){
        for (Archipelago archipelago: this.archipelagos) {
            archipelago.setColorThatDoesntCount(color);
        }
    }
    @Override
    public void playColorDoesntCount(Color color) throws MoveNotAvailableException {
        if(color == null) throw new IllegalArgumentException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.COLOR_DOESNT_COUNT.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        this.setColorThatDoesntCountForAllArchipelagos(color);

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }
    @Override
    public void resetColorThatDoesntCountForAllArchipelagos(){
        this.setColorThatDoesntCountForAllArchipelagos(null);
    }

    //10 OK
    @Override
    public void playSwapTwoStudentsBetweenEntranceAndDiningRoom(List<Color> students1, List<Color> students2) throws InvalidStudentListsLengthException, MoveNotAvailableException, StudentNotInTheEntranceException, FullDiningRoomLaneException, StudentsNotInTheDiningRoomException {
        if(students1 == null || students2 == null || students1.contains(null) || students2.contains(null)) throw new IllegalArgumentException();

        if(students1.size() != students2.size() || students2.size() > 3) throw new InvalidStudentListsLengthException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.SWAP_TWO_STUDENTS_BETWEEN_ENTRANCE_AND_DINING_ROOM.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        if(!this.getCurrentPlayerSchoolBoard().containsAllStudentsInTheDiningRoom(students1)) throw new StudentsNotInTheDiningRoomException();
        if(!this.getCurrentPlayerSchoolBoard().containsAllStudentsInTheEntrance(students2)) throw new StudentNotInTheEntranceException();

        for (Color student: students1 ) {
            this.getCurrentPlayerSchoolBoard().removeStudentFromDiningRoom(student);
            this.getCurrentPlayerSchoolBoard().addStudentToEntrance(student);
        }

        for (Color student: students2 ) {
            this.getCurrentPlayerSchoolBoard().removeStudentFromEntrance(student);
            this.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(student);
        }

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    //11 OK
    private void putOneStudentFromCharacterToDiningRoom(Color student) throws StudentNotOnCharacterException, FullDiningRoomLaneException {
        if(!characterPlayedInCurrentTurn.removeStudent(student)) throw new StudentNotOnCharacterException();

        this.getCurrentPlayerSchoolBoard().addStudentToDiningRoom(student);
    }
    @Override
    public void playPutOneStudentFromCharacterToDiningRoom(Color color) throws StudentNotOnCharacterException, FullDiningRoomLaneException, MoveNotAvailableException {
        if(color == null) throw new IllegalArgumentException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.PUT_ONE_STUDENT_FROM_CHARACTER_INTO_DINING_ROOM.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

        this.putOneStudentFromCharacterToDiningRoom(color);

        this.characterPlayedInCurrentTurn = selectedCharacter.get();

        this.payCharacter();
    }

    //12 OK
    private void putThreeStudentsInTheBag(Color color) {
        for (SchoolBoard schoolBoard: this.schoolBoards ) {
            for (int i = 0; i < 3; i++) {
                schoolBoard.removeStudentFromDiningRoom(color);
            }
        }
    }
    @Override
    public void playPutThreeStudentsInTheBag(Color color) throws MoveNotAvailableException {
        if(color == null) throw new IllegalArgumentException();

        Optional<PlayableCharacter> selectedCharacter = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.PUT_THREE_STUDENTS_IN_THE_BAG.characterId).findFirst();

        if(selectedCharacter.isEmpty()) throw new MoveNotAvailableException();

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
}
