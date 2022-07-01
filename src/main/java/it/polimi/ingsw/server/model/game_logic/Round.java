package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.ActionPhaseSubTurn;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;

import java.util.LinkedList;
import java.util.List;

public class Round {
    private List<Integer> roundOrder;
    private int iterator;

    private int currentRound;
    private boolean isLastRound;

    private Phase currentPhase;

    private ActionPhaseSubTurn actionPhaseSubTurn;

    public Round(List<Integer> roundOrder) {
        this.setNewRoundOrder(roundOrder);

        this.currentRound = 1;
        this.isLastRound = false;
        this.currentPhase = Phase.PLANNING;
        this.actionPhaseSubTurn = ActionPhaseSubTurn.STUDENTS_TO_MOVE;
    }

    /**
     * This method is used to set the new round order after every player played its card during the planning phase
     * @param roundOrder is a list containing the schoolBoard IDs ordered by the round order
     */
    public void setNewRoundOrder(List<Integer> roundOrder){
        this.roundOrder = roundOrder;
        this.iterator = 0;
    }

    /**
     * This method resets the internal iterator that iterates over the list representing the round order
     */
    public void resetIterator(){
        this.iterator = 0;
    }

    /**
     * This method returns true if a round is finished, false otherwise
     * @return true if a round is finished, false otherwise
     */
    public boolean isRoundDone(){
        return this.iterator >= this.roundOrder.size();
    }

    /**
     * This method returns the ID of the next schoolBoard that will play
     * @return an int representing the ID of the next schoolBoard that will play
     */
    public int next(){
        int next = this.roundOrder.get(iterator);
        this.iterator++;
        return next;
    }

    /**
     * This method returns the current round order
     * @return a list of IDs representing the current round order
     */
    public List<Integer> getRoundOrder(){
        return new LinkedList<>(this.roundOrder);
    }

    /**
     * This method increases the round count
     */
    public void increaseRoundCount(){
        this.currentRound++;
    }

    /**
     * This method returns true if the current round is also the last, false otherwise
     * @return true if the current round is also the last, false otherwise
     */
    public boolean isLastRound(){
        return this.isLastRound;
    }

    /**
     * This method returns the current round count
     * @return an int representing the current round count
     */
    public int getCurrentRound() {
        return this.currentRound;
    }

    /**
     * This method is used to set to true a flag that signals that the current round will be the last
     */
    public void setIsLastRoundTrue() {
        this.isLastRound = true;
    }

    /**
     * This method returns the current phase. It can be either PLANNING or ACTION
     * @return the current phase
     */
    public Phase getCurrentPhase() {
        return this.currentPhase;
    }

    /**
     * This method sets the current phase
     * @param currentPhase is the phase the game will be set to
     */
    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    /**
     * This method sets the phase of the ACTION phase sub-turn
     * @param actionPhaseSubTurn is the ActionPhaseSubTurn the game will be set to
     */
    public void setActionPhaseSubTurn(ActionPhaseSubTurn actionPhaseSubTurn) {
        this.actionPhaseSubTurn = actionPhaseSubTurn;
    }

    /**
     * This method returns the current action phase
     * @return the current action phase
     */
    public ActionPhaseSubTurn getActionPhaseSubTurn() {
        return this.actionPhaseSubTurn;
    }

    /**
     * This method returns true if the final round is done, false otherwise
     * @return true if the final round is done, false otherwise
     */
    public boolean isLastRoundDone() {
        return this.isLastRound() && this.isRoundDone() && this.getCurrentPhase() == Phase.ACTION;
    }
}
