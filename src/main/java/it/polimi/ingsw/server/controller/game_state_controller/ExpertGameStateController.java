package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.ExpertGameState;
import it.polimi.ingsw.server.model.game_logic.PlayableCharacter;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.Optional;

public class ExpertGameStateController extends GameStateController {
    public ExpertGameStateController(int playersNumber) throws GameStateInitializationFailureException {
        super(playersNumber);
    }

    @Override
    protected void initializeGameState(int playersNumber) throws GameStateInitializationFailureException {
        this.gameState = new ExpertGameState(playersNumber);
    }

    private void applyEffectGenericChecks(int characterIndex) throws WrongPhaseException, MoveAlreadyPlayedException, InvalidCharacterIndexException {
        if(this.gameState.getCurrentPhase() != Phase.ACTION) throw new WrongPhaseException();

        if(this.gameState.wasCharacterPlayedInCurrentTurn()) throw new MoveAlreadyPlayedException();

        if(characterIndex < 0 || characterIndex >= this.gameState.getAvailableCharacters().size()) throw new InvalidCharacterIndexException();
    }

    public boolean applyEffect(int characterIndex, int archipelagoIslandCode) throws InvalidCharacterIndexException, ArchipelagoAlreadyLockedException, InvalidArchipelagoIdException, WrongPhaseException, MoveAlreadyPlayedException, MoveNotAvailableException, NoAvailableLockException {

        this.applyEffectGenericChecks(characterIndex);

        boolean mergePerformed = false;

        Optional<Archipelago> selectedArchipelago = this.gameState.getArchipelagoFromSingleIslandCode(archipelagoIslandCode);

        if(selectedArchipelago.isEmpty()) throw new InvalidArchipelagoIdException();

        PlayableCharacter selectedCharacter = this.gameState.getAvailableCharacters().get(characterIndex);

        if(selectedCharacter.getCharacterId() == Character.LOCK_ARCHIPELAGO.characterId){
            if(!selectedCharacter.isLockAvailable()) throw new NoAvailableLockException();
            this.gameState.lockArchipelago(selectedArchipelago.get());
            selectedCharacter.useLock();
        }
        else if(selectedCharacter.getCharacterId() == Character.MOVE_MOTHER_NATURE_TO_ANY_ISLAND.characterId){
            int actualMotherNaturePositionIslandCode = this.gameState.getMotherNaturePositionIslandCodes().get(0);
            this.gameState.moveMotherNatureToGivenArchipelagoIslandCode(archipelagoIslandCode);

            if(this.getMostInfluentSchoolBoardIdOnMotherNaturesPosition().isPresent()){
                this.gameState.conquerArchipelago(this.getMostInfluentSchoolBoardIdOnMotherNaturesPosition().get());
                mergePerformed = this.merge();
            }
            this.gameState.moveMotherNatureToGivenArchipelagoIslandCode(actualMotherNaturePositionIslandCode);
        }
        else
            throw new MoveNotAvailableException();

        //this.gameState.payCharacter(this.gameState.getAvailableCharacters().get(characterIndex).getCurrentCost());
        this.gameState.playCharacter(selectedCharacter);

        return mergePerformed;
    }

    public void applyEffect(int characterIndex) throws WrongPhaseException, MoveAlreadyPlayedException, InvalidCharacterIndexException, MoveNotAvailableException {
        this.applyEffectGenericChecks(characterIndex);

        PlayableCharacter selectedCharacter = this.gameState.getAvailableCharacters().get(characterIndex);

        if(selectedCharacter.getCharacterId() == Character.GET_PROFESSORS_WITH_SAME_STUDENTS.characterId){
            //OK
            this.gameState.playCharacter(selectedCharacter);
        }
        else if(selectedCharacter.getCharacterId() == Character.TWO_ADDITIONAL_STEPS.characterId){
            //OK
            this.gameState.playCharacter(selectedCharacter);
        }
        else if(selectedCharacter.getCharacterId() == Character.TOWERS_DONT_COUNT.characterId){
            this.gameState.playCharacter(selectedCharacter);
        }
        else if(selectedCharacter.getCharacterId() == Character.TWO_ADDITIONAL_INFLUENCE.characterId){
            this.gameState.playCharacter(selectedCharacter);
        }
        else
            throw new MoveNotAvailableException();

    }

    /**
     * This method performs all the checks required by the rules and then, if all of them are met, modifies the gameState moving motherNature.
     *
     * @param nSteps indicates the number of steps for which the player intends to move motherNature
     * @throws InvalidNumberOfStepsException if the player provides a number of steps that isn't between 0 and the maximum number of steps that the player chose during the planning phase.
     * @throws WrongPhaseException           if the method is executed in the wrong phase.
     */
    @Override
    public boolean moveMotherNature(int nSteps) throws InvalidNumberOfStepsException, WrongPhaseException, MoreStudentsToBeMovedException, MoveAlreadyPlayedException {
        boolean merged = super.moveMotherNature(nSteps);
        this.gameState.unlockMotherNaturePosition();

        return merged;
    }
}