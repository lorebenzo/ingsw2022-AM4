package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.GameState;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.enums.ActionPhaseSubTurn;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.*;

public class GameStateController {
    private final GameState gameState;

    public GameStateController(int playersNumber) throws GameStateInitializationFailureException {

        //Create a new gameState
        this.gameState = new GameState(playersNumber);

        this.gameState.setCurrentPhase(Phase.PLANNING);
        try {
            this.gameState.fillClouds();
        } catch (EmptyStudentSupplyException ignored) { }
        this.gameState.setCurrentPlayerSchoolBoardId(this.gameState.getNextTurn());


        this.gameState.setActionPhaseSubTurn(ActionPhaseSubTurn.STUDENTS_TO_MOVE);

        //After the constructor ends, there is a round order based on how .stream().toList() ordered the elements of this.gameState.getSchoolBoardIds
        //Since the Phase is set to PLANNING, only the method playCard can be executed by players, in the order imposed by the iterator based on this.gameState.getRoundOrder
    }

    /**
     * ONLY FOR TESTING!
     * Creates a GameStateController for a game with 2 players
     */
    public GameStateController() throws GameStateInitializationFailureException, EmptyStudentSupplyException {

        //Create a new gameState
        this.gameState = new GameState(2);

        //Preparation of the roundOrder that will support the turns
        this.gameState.setRoundOrder(this.gameState.getSchoolBoardIds().stream().toList());
        this.gameState.resetRoundIterator();


        this.gameState.setCurrentPhase(Phase.PLANNING);
        this.gameState.fillClouds();
        this.gameState.setCurrentPlayerSchoolBoardId(this.gameState.getNextTurn());

        this.gameState.setActionPhaseSubTurn(ActionPhaseSubTurn.STUDENTS_TO_MOVE);

        //After the constructor ends, there is a round order based on how .stream().toList() ordered the elements of this.gameState.getSchoolBoardIds
        //Since the Phase is set to PLANNING, only the method playCard can be executed by players, in the order imposed by the iterator based on this.roundOrder
    }

    //Supported player moves that are package-private

    /**
     * This method gets a card in input, verifies if the current phase is PLANNING and then modifies the gameState accordingly
     * @param card is the card that the player chose to play
     * @throws CardIsNotInTheDeckException if the current player does not actually own the card to be played.
     * @throws InvalidCardPlayedException if another player already played the same card in this round, and it is not the final round.
     * @throws WrongPhaseException if the method is executed in the wrong phase.
     */
    public void playCard(Card card) throws CardIsNotInTheDeckException, InvalidCardPlayedException, WrongPhaseException, MoveAlreadyPlayedException {
        if(this.gameState.getCurrentPhase() != Phase.PLANNING) throw new WrongPhaseException();

        if(this.cardPlayed()) throw new MoveAlreadyPlayedException();

        this.gameState.playCard(card);
        this.nextPlanningTurn();
    }

    /**
     * This method performs all the checks required by the rules and then, if all of them are met, modifies the gameState moving the inputted student to its corresponding diningRoomLane.
     * @param student indicates the color of the player that has to be moved.
     * @throws StudentNotInTheEntranceException if the player that has to be moved is not actually in the entrance of the current player's schoolBoard.
     * @throws FullDiningRoomLaneException if the diningRoomLane corresponding to the student that has to be moved is already full.
     * @throws WrongPhaseException if the method is executed in the wrong phase.
     * @throws TooManyStudentsMovedException if the player has already moved the maximum number of students allowed by the rules.
     */
    public void moveStudentFromEntranceToDiningRoom(Color student) throws StudentNotInTheEntranceException, FullDiningRoomLaneException, WrongPhaseException, TooManyStudentsMovedException {
        if(this.gameState.getCurrentPhase() != Phase.ACTION) throw new WrongPhaseException();

        if(this.gameState.getActionPhaseSubTurn().compareTo(ActionPhaseSubTurn.STUDENTS_TO_MOVE) != 0) throw new TooManyStudentsMovedException();

        this.gameState.moveStudentFromEntranceToDiningRoom(student);

        this.checkStudentsToBeMoved();
        //assignProfessor() verifies on its own if the player should get the professor and does nothing if not
        this.assignProfessor(student);
    }


    /**
     * This method performs all the checks required by the rules and then, if all of them are met, modifies the gameState moving the inputted student to the inputted archipelago.
     * @param student indicates the color of the player that has to be moved.
     * @param archipelagoIslandCodes indicates the archipelago, identified through its islandCodes, onto which the student has to be moved.
     * @throws StudentNotInTheEntranceException if the player that has to be moved is not actually in the entrance of the current player's schoolBoard.
     * @throws WrongPhaseException if the method is executed in the wrong phase.
     * @throws TooManyStudentsMovedException if the player has already moved the maximum number of students allowed by the rules.
     */
    public void moveStudentFromEntranceToArchipelago(Color student, List<Integer> archipelagoIslandCodes) throws StudentNotInTheEntranceException, WrongPhaseException, TooManyStudentsMovedException {
        if(this.gameState.getCurrentPhase() != Phase.ACTION) throw new WrongPhaseException();

        if(this.gameState.getActionPhaseSubTurn().compareTo(ActionPhaseSubTurn.STUDENTS_TO_MOVE) != 0) throw new TooManyStudentsMovedException();

        this.gameState.moveStudentFromEntranceToArchipelago(student, archipelagoIslandCodes);

        this.checkStudentsToBeMoved();
    }


    /**
     * This method performs all the checks required by the rules and then, if all of them are met, modifies the gameState moving motherNature.
     * @param nSteps indicates the number of steps for which the player intends to move motherNature
     * @throws InvalidNumberOfStepsException if the player provides a number of steps that isn't between 0 and the maximum number of steps that the player chose during the planning phase.
     * @throws WrongPhaseException if the method is executed in the wrong phase.
     */
    public boolean moveMotherNature(int nSteps) throws InvalidNumberOfStepsException, WrongPhaseException, MoreStudentsToBeMovedException, MoveAlreadyPlayedException {

        boolean mergePerformed = false;

        if(this.gameState.getCurrentPhase() != Phase.ACTION) throw new WrongPhaseException();

        if(this.gameState.getActionPhaseSubTurn().compareTo(ActionPhaseSubTurn.MOTHER_NATURE_TO_MOVE) < 0) throw new MoreStudentsToBeMovedException();

        if(this.gameState.getActionPhaseSubTurn().compareTo(ActionPhaseSubTurn.MOTHER_NATURE_TO_MOVE) > 0) throw new MoveAlreadyPlayedException();

        this.gameState.moveMotherNatureNStepsClockwise(nSteps);

        this.gameState.setActionPhaseSubTurn(ActionPhaseSubTurn.STUDENTS_TO_GRAB);

        //If there is a player that is the most influent on an archipelago, he will conquer the archipelago
        if(this.getMostInfluentSchoolBoardIdOnMotherNaturesPosition().isPresent()){
            mergePerformed = this.gameState.conquerArchipelago(this.getMostInfluentSchoolBoardIdOnMotherNaturesPosition().get());

        }

        return  mergePerformed;

    }


    /**
     * This method performs all the checks required by the rules and then, if all of them are met, modifies the gameState grabbing the students from the chosen cloud.
     * @param cloudIndex indicates the index of the cloud from which the player wants to grab the students.
     * @throws EmptyCloudException indicates that che chosen cloud is empty.
     * @throws WrongPhaseException if the method is executed in the wrong phase.
     */
    public void grabStudentsFromCloud(int cloudIndex) throws EmptyCloudException, WrongPhaseException, MoveAlreadyPlayedException, MotherNatureToBeMovedException {
        if(this.gameState.getCurrentPhase() != Phase.ACTION) throw new WrongPhaseException();


        if(this.gameState.getActionPhaseSubTurn().compareTo(ActionPhaseSubTurn.STUDENTS_TO_GRAB) < 0) throw new MotherNatureToBeMovedException();


        if(this.gameState.getActionPhaseSubTurn().compareTo(ActionPhaseSubTurn.STUDENTS_TO_GRAB) > 0) throw new MoveAlreadyPlayedException();

        this.gameState.grabStudentsFromCloud(cloudIndex);

        this.gameState.setActionPhaseSubTurn(ActionPhaseSubTurn.TURN_TO_END);
    }


    /**
     * This method performs all the controls before ending the player's turn and starting the next player's turn
     * @throws MoreStudentsToBeMovedException if the player didn't move all the students that he has to move before trying to end his turn.
     * @throws MotherNatureToBeMovedException if the player didn't move motherNature before trying to end his turn.
     * @throws StudentsToBeGrabbedFromCloudException if the player didn't grab the students from a cloud before trying to end his turn.
     */
    public void endActionTurn() throws MoreStudentsToBeMovedException, MotherNatureToBeMovedException, StudentsToBeGrabbedFromCloudException, CardNotPlayedException, EmptyStudentSupplyException, WrongPhaseException {
        //TODO there may be more actions to be performed
        if(this.gameState.getCurrentPhase() != Phase.ACTION) throw new WrongPhaseException();

        if(this.gameState.getActionPhaseSubTurn().compareTo(ActionPhaseSubTurn.STUDENTS_TO_MOVE) == 0) throw new MoreStudentsToBeMovedException();


        if(this.gameState.getActionPhaseSubTurn().compareTo(ActionPhaseSubTurn.MOTHER_NATURE_TO_MOVE) == 0) throw new MotherNatureToBeMovedException();

        if(this.gameState.getActionPhaseSubTurn().compareTo(ActionPhaseSubTurn.TURN_TO_END) < 0) throw new StudentsToBeGrabbedFromCloudException();

        //Block rollback option
        this.nextActionTurn();
    }

    //Private methods


    public boolean cardPlayed(){
        return this.gameState.getSchoolBoardIdsToCardsPlayedThisRound().containsKey(this.gameState.getCurrentPlayerSchoolBoardId());
    }

    private void checkStudentsToBeMoved(){
        if((this.gameState.getInitialNumberOfStudentsInTheEntrance() - this.gameState.getNumberOfStudentsInTheEntrance()) >= this.gameState.getNumberOfMovableStudents())
            this.gameState.setActionPhaseSubTurn(ActionPhaseSubTurn.MOTHER_NATURE_TO_MOVE);

    }

    /***
     * This method defines the round order comparing the values of the cards played by different players
     */
    private void defineRoundOrder(){

        List<Map.Entry<Integer, Card>> orderedSchoolBoardsToCardPlayed = this.gameState.getSchoolBoardIdsToCardsPlayedThisRound().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .toList();

        this.gameState.setRoundOrder(orderedSchoolBoardsToCardPlayed.stream().map(Map.Entry::getKey).toList());

        this.gameState.resetRoundIterator();

    }

    private void nextPlanningTurn() {

        //If all the players played in this round, a new round will begin
        if(this.gameState.isLastTurnInThisRound()) {
            this.defineRoundOrder();
            this.gameState.resetRoundIterator();
            //If a planning round was completed, now the round order has to be defined and the phase has to be set to action



            this.gameState.setCurrentPhase(Phase.ACTION);
        }

        //There is still someone that didn't play, so they will play
        this.gameState.setCurrentPlayerSchoolBoardId(this.gameState.getNextTurn());

        this.gameState.setActionPhaseSubTurn(ActionPhaseSubTurn.STUDENTS_TO_MOVE);
    }


    private void nextActionTurn() throws EmptyStudentSupplyException {
        //If all the players played in this round, a new round will begin
        if(this.gameState.isLastTurnInThisRound()) {
            this.gameState.resetRoundIterator();
            this.gameState.resetSchoolBoardIdsToCardsPlayerThisRound();

            //If an actual round was completed, the round count has to be increased and a new round will begin with the planning phase
            this.gameState.increaseRoundCount();
            this.gameState.setCurrentPhase(Phase.PLANNING);
            this.gameState.fillClouds();
        }

        //There is still someone that didn't play, so they will play
        this.gameState.setCurrentPlayerSchoolBoardId(this.gameState.getNextTurn());

        this.gameState.setActionPhaseSubTurn(ActionPhaseSubTurn.STUDENTS_TO_MOVE);
    }

    Optional<Map<Integer, Boolean>> checkImmediateWinners(){
        return this.gameState.checkImmediateWinners();
    }


    int getCurrentPlayerSchoolBoardId(){
        return this.gameState.getCurrentPlayerSchoolBoardId();
    }

    Set<Integer> getSchoolBoardIds(){
        return this.gameState.getSchoolBoardIds();
    }

    /**
     * This method verifies if there is a schoolBoard that is more influent than all the others on the archipelago on which motherNature is,
     * and returns its schoolBoardId
     * @return an integer representing the schoolBoardId of the most influent player on the archipelago on which motherNature is
     */
    private Optional<Integer> getMostInfluentSchoolBoardIdOnMotherNaturesPosition(){
        return this.getMostInfluentSchoolBoardId(this.gameState.getMotherNaturePositionIslandCodes());

    }


    /**
     * This method verifies if there is a schoolBoard that is more influent than all the others on the archipelago on which motherNature is,
     * and returns its schoolBoardId
     * @return an integer representing the schoolBoardId of the most influent player on the archipelago on which motherNature is
     */
    private Optional<Integer> getMostInfluentSchoolBoardId(List<Integer> archipelagoIslandCodes){
        List<Map.Entry<Integer, Integer>> orderedPlayersInfluences = this.getInfluence(archipelagoIslandCodes).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();

        //If the number of players is 2 or 3, the most influent is calculated between the most influent and the second most influent.

        if(this.gameState.getNumberOfPlayers() == 2 || this.gameState.getNumberOfPlayers() == 3){
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


    /**
     * This method gets an archipelago in input and returns a map where every entry links a schoolBoard with its influence on the inputed archipelago
     * @param archipelagoIslandCodes is a List<Integer> uniquely identifying an archipelago
     * @return a Map<Integer, Integer> where the key is the schoolBoardId and the value is the influence on the inputed archipelago
     */
    private Map<Integer, Integer> getInfluence(List<Integer> archipelagoIslandCodes){
        return this.gameState.getInfluence(archipelagoIslandCodes);
    }


    /**
     * This method assigns the professor of the specified color to the current player, verifying if all the conditions are met.
     * @param professor indicates the color for which the professor may be assigned
     */
    private void assignProfessor(Color professor) {
        this.gameState.assignProfessor(professor);
    }

    /**
     * This method tries to merge the archipelago on which motherNature is with its left and its right neighbour
     * if the conditions to merge are met, the archipelagos will merge, if not, then nothing will happen
     */
/*    private boolean merge(){
        return this.gameState.mergeWithPrevious() || this.gameState.mergeWithNext();
    }*/


    LightGameState getLightGameState() {
        return this.gameState.lightify();
    }

    public GameState getGameStateForTesting(){
        return gameState;
    }

    public void setCurrentPhaseForTesting(Phase phase){
        this.gameState.setCurrentPhase(phase);
    }

}

