package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.ArchipelagoAlreadyLockedException;
import it.polimi.ingsw.server.model.game_logic.exceptions.EmptyStudentSupplyException;
import it.polimi.ingsw.server.model.game_logic.exceptions.GameStateInitializationFailureException;
import it.polimi.ingsw.server.model.game_logic.exceptions.InvalidCharacterIndexException;

import java.util.*;

public class ExpertGameState extends GameState {

    private final List<PlayableCharacter> availableCharacters;
    private PlayableCharacter characterPlayedInCurrentTurn;


    /**
     * @param numberOfPlayers number of players in the game, must be between 2 (inclusive) and 4 (inclusive)
     * @throws IllegalArgumentException                if the argument representing the number of players is not between 2 and 4
     * @throws GameStateInitializationFailureException if there is a problem in the initialization of the archipelagos
     *                                                 or the schoolBoards,
     *                                                 or the clouds.
     */
    public ExpertGameState(int numberOfPlayers) throws GameStateInitializationFailureException {
        super(numberOfPlayers);
        this.availableCharacters = new ArrayList<>();

        this.extractCharacters();
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
    private void extractCharacters() {
        List<Character> characters = new ArrayList<>(List.of(Character.values()));
        Collections.shuffle(characters);


        for (int i = 0; i < 3; i++)
            this.availableCharacters.add(PlayableCharacter.initializeCharacter(characters.get(i)));
    }

    @Override
    protected ExpertSchoolBoard getCurrentPlayerSchoolBoard() {
        return (ExpertSchoolBoard) super.getCurrentPlayerSchoolBoard();
    }

    public void playCharacter(PlayableCharacter playableCharacter) throws InvalidCharacterIndexException {
        if(playableCharacter == null) throw new IllegalArgumentException();

        if(!this.availableCharacters.contains(playableCharacter)) throw new InvalidCharacterIndexException();

        this.characterPlayedInCurrentTurn = playableCharacter;

        if(this.characterPlayedInCurrentTurn.getCharacterId() == Character.TOWERS_DONT_COUNT.characterId)
            this.setIfTowersDoCountForAllArchipelagos(false);

        this.payCharacter();

    }

    private void setIfTowersDoCountForAllArchipelagos(boolean doTowersCount){
        for (Archipelago archipelago: this.archipelagos) {
            archipelago.setIfTowersDoCount(doTowersCount);
        }
    }

    /**
     * This method gets an archipelago in input and returns a map where every entry links a schoolBoard with its influence on the inputted archipelago
     *
     * @param archipelagoIslandCodes is a List<Integer> uniquely identifying an archipelago
     * @return a Map<Integer, Integer> where the key is the schoolBoardId and the value is the influence on the inputted archipelago
     */
    @Override
    public Optional<Map<Integer, Integer>> getInfluence(List<Integer> archipelagoIslandCodes) {
        Optional<Map<Integer, Integer>> tmp = super.getInfluence(archipelagoIslandCodes);
        if(tmp.isPresent())
            if(this.characterPlayedInCurrentTurn.getCharacterId() == Character.TWO_ADDITIONAL_INFLUENCE.characterId)
                tmp.get().put(this.getCurrentPlayerSchoolBoardId(), tmp.get().get(this.getCurrentPlayerSchoolBoardId()));

        return tmp;
    }

    private Optional<PlayableCharacter> getCharacterFromCharacterIndex(int characterIndex){
        if(characterIndex >= 0 && characterIndex < 3)
            return Optional.of(this.availableCharacters.get(characterIndex));
        else
            return Optional.empty();
    }

    private void payCharacter() {
        this.getCurrentPlayerSchoolBoard().payCharacter(this.characterPlayedInCurrentTurn.getCurrentCost());
        this.characterPlayedInCurrentTurn.increaseCost();
    }

    public List<PlayableCharacter> getAvailableCharacters(){
        return new ArrayList<>(this.availableCharacters);
    }


    public void lockArchipelago(Archipelago archipelagoToLock) throws ArchipelagoAlreadyLockedException {
        if(archipelagoToLock == null) throw new IllegalArgumentException();

        archipelagoToLock.lock();
    }


    public boolean wasCharacterPlayedInCurrentTurn() {
        return this.characterPlayedInCurrentTurn != null;
    }

    /**
     * The current player conquers the archipelago mother nature is currently in, placing a tower of his own color
     * and substituting any tower that was previously placed on that archipelago
     * //@throws InvalidSchoolBoardIdException if the current player's school board id is invalid
     *
     * @param schoolBoardId is the schoolboard of the most influent player on the motherNaturePosition
     */
    @Override
    public void conquerArchipelago(int schoolBoardId) {
        if(!motherNaturePosition.isLocked())
            super.conquerArchipelago(schoolBoardId);
    }

    public void unlockMotherNaturePosition(){
        if(this.motherNaturePosition.isLocked()){
            this.motherNaturePosition.unlock();
            var tmp = this.availableCharacters.stream().filter(character -> character.getCharacterId() == Character.LOCK_ARCHIPELAGO.characterId).findFirst();
            tmp.ifPresent(Playable::unLock);
        }
    }

    @Override
    public int getNextTurn() {
        this.characterPlayedInCurrentTurn = null;
        this.resetProfessors();
        this.setIfTowersDoCountForAllArchipelagos(true);
        return super.getNextTurn();
    }

    private void resetProfessors(){
        for (Color professor: Color.values()) {
            this.assignProfessor(professor);
        }
    }

    //Modifies the assignment of professors when a character is played
    @Override
    protected boolean compareCurrentPlayersStudentsNumberWithOthersMax(int currentPlayerNumberOfStudentsInDiningRoomLane, int otherSchoolBoardsMaxStudentsInDiningRoomLane) {
        if(this.characterPlayedInCurrentTurn.getCharacterId() == Character.GET_PROFESSORS_WITH_SAME_STUDENTS.characterId)
            return currentPlayerNumberOfStudentsInDiningRoomLane >= otherSchoolBoardsMaxStudentsInDiningRoomLane;
        return super.compareCurrentPlayersStudentsNumberWithOthersMax(currentPlayerNumberOfStudentsInDiningRoomLane,otherSchoolBoardsMaxStudentsInDiningRoomLane);
    }

    public int getAdditionalSteps() {
        if(this.characterPlayedInCurrentTurn.getCharacterId() == Character.TWO_ADDITIONAL_STEPS.characterId)
            return 2;
        else
            return 0;
    }


    @Override
    protected int getAllowedStepsNumber() {
        return super.getAllowedStepsNumber() + this.getAdditionalSteps();
    }
}
