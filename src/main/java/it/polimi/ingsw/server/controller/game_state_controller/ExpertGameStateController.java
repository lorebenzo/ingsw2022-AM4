package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.ExpertGameState;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.List;
import java.util.UUID;

public class ExpertGameStateController extends GameStateController {

    public ExpertGameStateController(int playersNumber) throws GameStateInitializationFailureException {
        super(playersNumber);
    }

    public ExpertGameStateController(UUID gameUUID) {
        super(gameUUID);
    }

    /**
     * This method initializes the correct gameState with its attributes
     * @param playersNumber is the number of players
     * @return the newly created GameState
     * @throws GameStateInitializationFailureException if there was a failure in the initialization procedure
     */
    @Override
    protected ExpertGameState initializeGameState(int playersNumber) throws GameStateInitializationFailureException {
        return new ExpertGameState(playersNumber);
    }

    /**
     * This method performs the generic checks on the input before applying the effect
     * @param characterIndex is the index of the character that the player inputted
     * @throws WrongPhaseException if the effect is activated during the wrong phase
     * @throws MoveAlreadyPlayedException if another character was activated during this turn
     * @throws InvalidCharacterIndexException if the character index is outside the bounds of the availableCharacters list
     */
    private void applyEffectGenericChecks(int characterIndex) throws WrongPhaseException, MoveAlreadyPlayedException, InvalidCharacterIndexException {
        if(this.gameState.getCurrentPhase() != Phase.ACTION) throw new WrongPhaseException();

        if(this.gameState.wasCharacterPlayedInCurrentTurn()) throw new MoveAlreadyPlayedException();

        if(characterIndex < 0 || characterIndex >= this.gameState.getAvailableCharacters().size()) throw new InvalidCharacterIndexException();
    }

    /**
     * This method applies the effect of the character corresponding to the inputted character index.
     * @param characterIndex is the index of the character to be activated
     * @throws WrongPhaseException if the effect is activated during the wrong phase
     * @throws MoveAlreadyPlayedException if another character was activated during this turn
     * @throws InvalidCharacterIndexException if the character index is outside the bounds of the availableCharacters list
     * @throws MoveNotAvailableException if the move is not present in availableCharacters
     * @throws WrongArgumentsException if the character linked to the inputted index and the list of arguments are incompatible
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void applyEffect(int characterIndex) throws WrongPhaseException, MoveAlreadyPlayedException, InvalidCharacterIndexException, MoveNotAvailableException, WrongArgumentsException, NotEnoughCoinsException {
        this.applyEffectGenericChecks(characterIndex);

        int characterId = this.gameState.getAvailableCharacters().get(characterIndex).getCharacterId();

        if(characterId == Character.GET_PROFESSORS_WITH_SAME_STUDENTS.characterId){
            this.gameState.playGetProfessorsWithSameStudents();
        }else if (characterId == Character.TWO_ADDITIONAL_STEPS.characterId){
            this.gameState.playTwoAdditionalSteps();
        }else if(characterId == Character.TOWERS_DONT_COUNT.characterId){
            this.gameState.playTowersDontCount();
        } else if(characterId == Character.TWO_ADDITIONAL_INFLUENCE.characterId){
            this.gameState.playTwoAdditionalInfluence();
        }else
            throw new WrongArgumentsException();

    }

    /**
     * This method applies the effect of the character corresponding to the inputted character index.
     * @param characterIndex is the index of the character to be activated
     * @param archipelagoIslandCode is a code that identifies a single archipelago
     * @return true if a merge was performed, false otherwise
     * @throws InvalidCharacterIndexException if the character index is outside the bounds of the availableCharacters list
     * @throws ArchipelagoAlreadyLockedException if the archipelago to be locked is already locked
     * @throws InvalidArchipelagoIdException if the inputted archipelago code is invalid
     * @throws WrongPhaseException if the effect is activated during the wrong phase
     * @throws MoveAlreadyPlayedException if another character was activated during this turn
     * @throws MoveNotAvailableException if the move is not present in availableCharacters
     * @throws NoAvailableLockException if no locks are available on the character
     * @throws WrongArgumentsException if the character linked to the inputted index and the list of arguments are incompatible
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     * @throws GameOverException if one of the gameOver conditions is met with the activation of the effect
     */
    @Override
    public boolean applyEffect(int characterIndex, int archipelagoIslandCode) throws InvalidCharacterIndexException, ArchipelagoAlreadyLockedException, InvalidArchipelagoIdException, WrongPhaseException, MoveAlreadyPlayedException, MoveNotAvailableException, NoAvailableLockException, WrongArgumentsException, NotEnoughCoinsException, GameOverException {

        boolean mergePerformed = false;

        this.applyEffectGenericChecks(characterIndex);

        int characterId = this.gameState.getAvailableCharacters().get(characterIndex).getCharacterId();

        if(characterId == Character.LOCK_ARCHIPELAGO.characterId){
            this.gameState.playCharacterLock(archipelagoIslandCode);
        }
        else if(characterId == Character.MOVE_MOTHER_NATURE_TO_ANY_ARCHIPELAGO.characterId){
            mergePerformed = this.gameState.playMoveMotherNatureToAnyArchipelago(archipelagoIslandCode);
            if(this.gameState.checkWinners().containsValue(true)) throw new GameOverException(this.gameState.checkWinners());
        }
        else
            throw new WrongArgumentsException();

        return mergePerformed;
    }

    /**
     * This method applies the effect of the character corresponding to the inputted character index.
     * @param characterIndex is the index of the character to be activated
     * @param color is the color involved in the effect of the character
     * @throws InvalidCharacterIndexException if the character index is outside the bounds of the availableCharacters list
     * @throws MoveAlreadyPlayedException if another character was activated during this turn
     * @throws WrongPhaseException if the effect is activated during the wrong phase
     * @throws MoveNotAvailableException if the move is not present in availableCharacters
     * @throws StudentNotOnCharacterException if the inputted student is not present on the character
     * @throws FullDiningRoomLaneException if the diningRoom table corresponding to the inputted student is already full
     * @throws WrongArgumentsException if the character linked to the inputted index and the list of arguments are incompatible
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     * @throws StudentsNotInTheDiningRoomException if the inputted student is not present in the diningRoom
     */
    @Override
    public void applyEffect(int characterIndex, Color color) throws InvalidCharacterIndexException, MoveAlreadyPlayedException, WrongPhaseException, MoveNotAvailableException, StudentNotOnCharacterException, FullDiningRoomLaneException, WrongArgumentsException, NotEnoughCoinsException, StudentsNotInTheDiningRoomException {
        this.applyEffectGenericChecks(characterIndex);

        int characterId = this.gameState.getAvailableCharacters().get(characterIndex).getCharacterId();

        if(characterId == Character.COLOR_DOESNT_COUNT.characterId){
            this.gameState.playColorDoesntCount(color);
        }else if(characterId == Character.PUT_ONE_STUDENT_FROM_CHARACTER_INTO_DINING_ROOM.characterId){
            this.gameState.playPutOneStudentFromCharacterToDiningRoom(color);
        }else if(characterId == Character.PUT_THREE_STUDENTS_IN_THE_BAG.characterId){
            this.gameState.playPutThreeStudentsInTheBag(color);
        }
        else
            throw new WrongArgumentsException();
    }

    /**
     * This method applies the effect of the character corresponding to the inputted character index.
     * @param characterIndex is the index of the character to be activated
     * @param color is the color involved in the effect of the character
     * @param archipelagoIslandCode is a code that identifies a single archipelago
     * @throws InvalidCharacterIndexException if the character index is outside the bounds of the availableCharacters list
     * @throws MoveAlreadyPlayedException if another character was activated during this turn
     * @throws WrongPhaseException if the effect is activated during the wrong phase
     * @throws MoveNotAvailableException if the move is not present in availableCharacters
     * @throws InvalidArchipelagoIdException if the inputted archipelago code is invalid
     * @throws StudentNotOnCharacterException if the inputted student is not present on the character
     * @throws WrongArgumentsException if the character linked to the inputted index and the list of arguments are incompatible
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void applyEffect(int characterIndex, Color color, int archipelagoIslandCode) throws InvalidCharacterIndexException, MoveAlreadyPlayedException, WrongPhaseException, MoveNotAvailableException, InvalidArchipelagoIdException, StudentNotOnCharacterException, WrongArgumentsException, NotEnoughCoinsException {
        this.applyEffectGenericChecks(characterIndex);

        int characterId = this.gameState.getAvailableCharacters().get(characterIndex).getCharacterId();

        if(characterId == Character.PUT_ONE_STUDENT_FROM_CHARACTER_TO_ARCHIPELAGO.characterId)
            this.gameState.playPutOneStudentFromCharacterToArchipelago(color, archipelagoIslandCode);
        else
            throw new WrongArgumentsException();
    }

    /**
     * This method applies the effect of the character corresponding to the inputted character index.
     * @param characterIndex is the index of the character to be activated
     * @param getStudents is the list of students to get
     * @param giveStudents is the list of students to give
     * @throws InvalidCharacterIndexException if the character index is outside the bounds of the availableCharacters list
     * @throws MoveAlreadyPlayedException if another character was activated during this turn
     * @throws WrongPhaseException if the effect is activated during the wrong phase
     * @throws WrongArgumentsException if the character linked to the inputted index and the list of arguments are incompatible
     * @throws InvalidStudentListsLengthException if the length of the getStudents and giveStudents is not equal
     * @throws StudentNotInTheEntranceException if one of the elements of getStudents is not actually present in the entrance
     * @throws StudentNotOnCharacterException if the inputted student is not present on the character
     * @throws MoveNotAvailableException if the move is not present in availableCharacters
     * @throws StudentsNotInTheDiningRoomException if the inputted student is not present in the diningRoom
     * @throws FullDiningRoomLaneException if the diningRoom table corresponding to the inputted student is already full
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to activate the character
     */
    @Override
    public void applyEffect(int characterIndex, List<Color> getStudents, List<Color> giveStudents) throws InvalidCharacterIndexException, MoveAlreadyPlayedException, WrongPhaseException, WrongArgumentsException, InvalidStudentListsLengthException, StudentNotInTheEntranceException, StudentNotOnCharacterException, MoveNotAvailableException, StudentsNotInTheDiningRoomException, FullDiningRoomLaneException, NotEnoughCoinsException {
        this.applyEffectGenericChecks(characterIndex);

        int characterId = this.gameState.getAvailableCharacters().get(characterIndex).getCharacterId();

        if(characterId == Character.SWAP_THREE_STUDENTS_BETWEEN_CHARACTER_AND_ENTRANCE.characterId)
            this.gameState.playSwapThreeStudentsBetweenCharacterAndEntrance(getStudents, giveStudents);
        else if(characterId == Character.SWAP_TWO_STUDENTS_BETWEEN_ENTRANCE_AND_DINING_ROOM.characterId)
            this.gameState.playSwapTwoStudentsBetweenEntranceAndDiningRoom(getStudents, giveStudents);
        else
            throw new WrongArgumentsException();
    }

    /**
     * This method performs all the checks required by the rules and then, if all of them are met, modifies the gameState moving motherNature.
     *
     * @param nSteps indicates the number of steps for which the player intends to move motherNature
     * @throws InvalidNumberOfStepsException if the player provides a number of steps that isn't between 0 and the maximum number of steps that the player chose during the planning phase.
     * @throws WrongPhaseException           if the method is executed in the wrong phase.
     */
    @Override
    public boolean moveMotherNature(int nSteps) throws InvalidNumberOfStepsException, WrongPhaseException, MoreStudentsToBeMovedException, MoveAlreadyPlayedException, GameOverException {
        boolean merged = super.moveMotherNature(nSteps);
        this.gameState.unlockMotherNaturePosition();

        return merged;
    }

    /**
     * This method advances the turn during the action phase, after a player ended its turn.
     * @throws GameOverException if a gameOver condition has occurred
     */
    @Override
    protected void nextActionTurn() throws GameOverException {
        try{
            this.gameState.refillCharacter();
        } catch (EmptyStudentSupplyException ignored){
            this.gameState.setLastRoundTrue();
        }

        this.gameState.assignProfessorsAfterEffect();

        this.gameState.resetCharacterPlayedThisTurn();


        this.gameState.setTowersInfluenceForAllArchipelagos(true);
        this.gameState.resetColorThatDoesntCountForAllArchipelagos();

        super.nextActionTurn();
    }

    @Override
    public void rollback() {}


}