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

    private void applyEffectGenericChecks(int characterIndex) throws WrongPhaseException, MoveAlreadyPlayedException, InvalidCharacterIndexException {
        if(this.gameState.getCurrentPhase() != Phase.ACTION) throw new WrongPhaseException();

        if(this.gameState.wasCharacterPlayedInCurrentTurn()) throw new MoveAlreadyPlayedException();

        if(characterIndex < 0 || characterIndex >= this.gameState.getAvailableCharacters().size()) throw new InvalidCharacterIndexException();
    }

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

    @Override
    public void applyEffect(int characterIndex, Color color, int archipelagoIslandCode) throws InvalidCharacterIndexException, MoveAlreadyPlayedException, WrongPhaseException, MoveNotAvailableException, InvalidArchipelagoIdException, StudentNotOnCharacterException, WrongArgumentsException, NotEnoughCoinsException {
        this.applyEffectGenericChecks(characterIndex);

        int characterId = this.gameState.getAvailableCharacters().get(characterIndex).getCharacterId();

        if(characterId == Character.PUT_ONE_STUDENT_FROM_CHARACTER_TO_ARCHIPELAGO.characterId)
            this.gameState.playPutOneStudentFromCharacterToArchipelago(color, archipelagoIslandCode);
        else
            throw new WrongArgumentsException();
    }

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