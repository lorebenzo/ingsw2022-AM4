package it.polimi.ingsw.server.game_logic.Controllers;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.server.game_logic.GameState;
import it.polimi.ingsw.server.game_logic.enums.Card;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.exceptions.*;

import java.util.*;
import java.util.stream.Collectors;

public class GameStateController {
    private final GameState gameState;

    private Map<Peer,Integer> peersToSchoolBoardIdsMap;

    private List<Integer> roundOrder;
    private Iterator<Integer> roundIterator;

    //private int numberOfStudentsMoved;
    private boolean motherNatureMoved;
    private boolean studentsGrabbedFromCloud;


    public GameStateController(List<Peer> peers) throws GameStateInitializationFailureException {

        //Create a new gameState
        this.gameState = new GameState(peers.size());

        //Create a map that links every peer to a schoolBoardId
        Iterator<Integer> schoolBoardIdsSetIterator = this.gameState.getSchoolBoardIds().iterator();

        for (int i = 0; i < this.gameState.getNumberOfPlayers(); i++){
            this.peersToSchoolBoardIdsMap = new HashMap<>();

            this.peersToSchoolBoardIdsMap.put(peers.get(i), schoolBoardIdsSetIterator.next());
        }

        //Preparation of the roundOrder that will support the turns
        this.roundOrder = this.gameState.getSchoolBoardIds().stream().toList();
        this.roundIterator = this.roundOrder.listIterator();


        this.gameState.setCurrentPhase(GameState.Phase.PLANNING);
        this.gameState.setCurrentPlayerSchoolBoardId(this.roundIterator.next());

        //this.numberOfStudentsMoved = 0;
        this.motherNatureMoved = false;
        this.studentsGrabbedFromCloud = false;


        //After the constructor ends, there is a round order based on how .stream().toList() ordered the elements of this.gameState.getSchoolBoardIds
        //Since the Phase is set to PLANNING, only the method playCard can be executed by players, in the order imposed by the iterator based on this.roundOrder
    }


    //Only for testing
    public GameStateController() throws GameStateInitializationFailureException {

        //Create a new gameState
        this.gameState = new GameState(2);

        //Create a map that links every peer to a schoolBoardId
        Iterator<Integer> schoolBoardIdsSetIterator = this.gameState.getSchoolBoardIds().iterator();

        this.peersToSchoolBoardIdsMap = new HashMap<>();

        //Preparation of the roundOrder that will support the turns
        this.roundOrder = this.gameState.getSchoolBoardIds().stream().toList();
        this.roundIterator = this.roundOrder.listIterator();


        this.gameState.setCurrentPhase(GameState.Phase.PLANNING);
        this.gameState.setCurrentPlayerSchoolBoardId(this.roundIterator.next());

        //this.numberOfStudentsMoved = 0;
        this.motherNatureMoved = false;
        this.studentsGrabbedFromCloud = false;


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
    public void playCard(Card card) throws CardIsNotInTheDeckException, /*InvalidSchoolBoardIdException,*/ InvalidCardPlayedException, WrongPhaseException, MoveAlreadyPlayedException, EmptyStudentSupplyException {
        if(this.gameState.getCurrentPhase() != GameState.Phase.PLANNING) throw new WrongPhaseException();

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
    public void moveStudentFromEntranceToDiningRoom(Color student) throws /*InvalidSchoolBoardIdException,*/ StudentNotInTheEntranceException, FullDiningRoomLaneException, WrongPhaseException, TooManyStudentsMovedException {
        if(this.gameState.getCurrentPhase() != GameState.Phase.ACTION) throw new WrongPhaseException();

        if(!this.availableStudentsToBeMoved() || this.motherNatureMoved) throw new TooManyStudentsMovedException();

        this.gameState.moveStudentFromEntranceToDiningRoom(student);
        //this.numberOfStudentsMoved++;
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
    public void moveStudentFromEntranceToArchipelago(Color student, List<Integer> archipelagoIslandCodes) throws /*InvalidSchoolBoardIdException,*/ StudentNotInTheEntranceException, WrongPhaseException, TooManyStudentsMovedException {
        if(this.gameState.getCurrentPhase() != GameState.Phase.ACTION) throw new WrongPhaseException();

        if(!this.availableStudentsToBeMoved() || this.motherNatureMoved) throw new TooManyStudentsMovedException();

        this.gameState.moveStudentFromEntranceToArchipelago(student, archipelagoIslandCodes);
        //this.numberOfStudentsMoved++;
    }


    /**
     * This method performs all the checks required by the rules and then, if all of them are met, modifies the gameState moving motherNature.
     * @param nSteps indicates the number of steps for which the player intends to move motherNature
     * @throws InvalidNumberOfStepsException if the player provides a number of steps that isn't between 0 and the maximum number of steps that the player chose during the planning phase.
     * @throws WrongPhaseException if the method is executed in the wrong phase.
     */
    public boolean moveMotherNature(int nSteps) throws InvalidNumberOfStepsException, /*InvalidSchoolBoardIdException,*/ WrongPhaseException, MoreStudentsToBeMovedException, MoveAlreadyPlayedException {
        boolean mergePerformed = false;

        if(this.gameState.getCurrentPhase() != GameState.Phase.ACTION) throw new WrongPhaseException();

        if(this.availableStudentsToBeMoved() && !this.studentsGrabbedPerformed()) throw new MoreStudentsToBeMovedException();

        if(this.motherNatureMoved) throw new MoveAlreadyPlayedException();

        this.gameState.moveMotherNatureNStepsClockwise(nSteps);
        this.motherNatureMoved = true;

        //If there is a player that is the most influent on an archipelago, he will conquer the archipelago
        if(this.getMostInfluentSchoolBoardIdOnMotherNaturesPosition().isPresent()){
            this.gameState.conquerArchipelago(this.getMostInfluentSchoolBoardIdOnMotherNaturesPosition().get());
            mergePerformed = this.merge();
        }

        return mergePerformed;

    }


    /**
     * This method performs all the checks required by the rules and then, if all of them are met, modifies the gameState grabbing the students from the chosen cloud.
     * @param cloudIndex indicates the index of the cloud from which the player wants to grab the students.
     * @throws EmptyCloudException indicates that che chosen cloud is empty.
     * @throws WrongPhaseException if the method is executed in the wrong phase.
     */
    public void grabStudentsFromCloud(int cloudIndex) throws /*InvalidSchoolBoardIdException,*/ EmptyCloudException, WrongPhaseException, MoveAlreadyPlayedException, MotherNatureToBeMovedException {
        if(this.gameState.getCurrentPhase() != GameState.Phase.ACTION) throw new WrongPhaseException();

        if(!this.motherNatureMoved) throw new MotherNatureToBeMovedException();

        //if(this.availableStudentsToBeMoved()) throw new MoreStudentsToBeMovedException();

        if(this.studentsGrabbedPerformed()) throw new MoveAlreadyPlayedException();

        this.gameState.grabStudentsFromCloud(cloudIndex);
        this.studentsGrabbedFromCloud = true;
    }


    /**
     * This method performs all the controls before ending the player's turn and starting the next player's turn
     * @throws GameOverException if the student supply is empty
     * @throws MoreStudentsToBeMovedException if the player didn't move all the students that he has to move before trying to end his turn.
     * @throws MotherNatureToBeMovedException if the player didn't move motherNature before trying to end his turn.
     * @throws StudentsToBeGrabbedFromCloudException if the player didn't grab the students from a cloud before trying to end his turn.
     */
    public void endActionTurn() throws /*FullCloudException, */GameOverException, MoreStudentsToBeMovedException, MotherNatureToBeMovedException, StudentsToBeGrabbedFromCloudException, CardNotPlayedException {
        //TODO there may be more actions to be performed
        if(availableStudentsToBeMoved()) throw new MoreStudentsToBeMovedException();

        if(!this.motherNatureMoved) throw new MotherNatureToBeMovedException();

        if(!this.studentsGrabbedPerformed()) throw new StudentsToBeGrabbedFromCloudException();

        //Block rollback option
        this.nextActionTurn();
    }

    //Necessary to CommunicationController

    /**
     * This method whether a move was sent by the current player or not.
     * @param peer indicates a  peer, therefore a player,
     * @return true if the move has to be processed, because it is performed by the current player, false otherwise.
     */
    boolean isMoveFromCurrentPlayer(Peer peer){
        return this.getSchoolBoardIdFromPeer(peer) == this.gameState.getCurrentPlayerSchoolBoardId();
    }

    //Private methods


    public boolean cardPlayed(){
        return this.gameState.getSchoolBoardIdsToCardsPlayedThisRound().containsKey(this.gameState.getCurrentPlayerSchoolBoardId());
    }

    private boolean availableStudentsToBeMoved(){
        if(!studentsGrabbedPerformed())
            return (this.gameState.getInitialNumberOfStudentsInTheEntrance() - this.gameState.getNumberOfStudentsInTheEntrance()) < this.gameState.getNumberOfMovableStudents();
        else
            return false;
        //return this.numberOfStudentsMoved <= this.gameState.getNumberOfMovableStudents();

    }

    //TODO improve logic to remove this.studentsGrabbedFromCloud
    private boolean studentsGrabbedPerformed(){
/*      if(this.gameState.getNumberOfStudentsInTheEntrance() == this.gameState.getInitialNumberOfStudentsInTheEntrance() && !availableStudentsToBeMoved())
            return true;
        else
            return false;*/
        return this.studentsGrabbedFromCloud;
    }

    /***
     * This method defines the round order comparing the values of the cards played by different players
     */
    private void defineRoundOrder(){

        List<Map.Entry<Integer, Card>> orderedSchoolBoardsToCardPlayed = this.gameState.getSchoolBoardIdsToCardsPlayedThisRound().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .toList();

        this.roundOrder = orderedSchoolBoardsToCardPlayed.stream().map(Map.Entry::getKey).toList();

        this.roundIterator = this.roundOrder.listIterator();

    }

    private void nextPlanningTurn() throws EmptyStudentSupplyException  /* throws FullCloudException,*/ {

        //If all the players played in this round, a new round will begin
        if(!this.roundIterator.hasNext()) {
            this.defineRoundOrder();
            this.roundIterator = this.roundOrder.listIterator();
            //If a planning round was completed, now the round order has to be defined and the phase has to be set to action

            this.gameState.fillClouds();

            this.gameState.setCurrentPhase(GameState.Phase.ACTION);
        }

        //There is still someone that didn't play, so they will play
        this.gameState.setCurrentPlayerSchoolBoardId(this.roundIterator.next());

        this.motherNatureMoved = false;
        this.studentsGrabbedFromCloud = false;
    }


    private void nextActionTurn() /*throws FullCloudException,*/ {
        //If all the players played in this round, a new round will begin
        if(!this.roundIterator.hasNext()) {
            this.roundIterator = this.roundOrder.listIterator();
            this.gameState.resetSchoolBoardIdsToCardsPlayerThisRound();

            //If an actual round was completed, the round count has to be increased and a new round will begin with the planning phase
            this.gameState.increaseRoundCount();
            this.gameState.setCurrentPhase(GameState.Phase.PLANNING);
        }

        //There is still someone that didn't play, so they will play
        this.gameState.setCurrentPlayerSchoolBoardId(this.roundIterator.next());

        this.motherNatureMoved = false;
        this.studentsGrabbedFromCloud = false;
    }

    private Peer getPeerFromSchoolBoardId(int schoolBoardId){
        return this.peersToSchoolBoardIdsMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == schoolBoardId)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .get(0);
    }

    private int getSchoolBoardIdFromPeer(Peer peer){
        return this.peersToSchoolBoardIdsMap.get(peer);
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
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) //sorts the map using the influences in descending order
                .collect(Collectors.toList());

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
     * //@throws InvalidSchoolBoardIdException if there is an error with the schoolBoardIds
     */
    private void assignProfessor(Color professor) /*throws InvalidSchoolBoardIdException*/ {
        this.gameState.assignProfessor(professor);
    }

    /**
     * This method tries to merge the archipelago on which motherNature is with its left and its right neighbour
     * if the conditions to merge are met, the archipelagos will merge, if not, then nothing will happen
     */
    private boolean merge(){
        return this.gameState.mergeWithPrevious() || this.gameState.mergeWithNext();
    }


    public GameState getGameStateForTesting(){
        return gameState;
    }

    public void setCurrentPhaseForTesting(GameState.Phase phase){
        this.gameState.setCurrentPhase(phase);
    }

}

