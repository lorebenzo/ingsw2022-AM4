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

    public void setNewRoundOrder(List<Integer> roundOrder){
        this.roundOrder = roundOrder;
        this.iterator = 0;
    }

    public void resetIterator(){
        this.iterator = 0;
    }

    public boolean isRoundDone(){
        return this.iterator >= this.roundOrder.size();
    }

    public int next(){
        int next = this.roundOrder.get(iterator);
        this.iterator++;
        return next;
    }
    public List<Integer> getRoundOrder(){
        return new LinkedList<>(this.roundOrder);
    }

    public void increaseRoundCount(){
        this.currentRound++;
    }

    public boolean isLastRound(){
        return this.isLastRound;
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    public void setIsLastRoundTrue() {
        this.isLastRound = true;
    }

    public Phase getCurrentPhase() {
        return this.currentPhase;
    }

    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public void setActionPhaseSubTurn(ActionPhaseSubTurn studentsToMove) {
        this.actionPhaseSubTurn = studentsToMove;
    }

    public ActionPhaseSubTurn getActionPhaseSubTurn() {
        return this.actionPhaseSubTurn;
    }

    public boolean isLastRoundDone() {
        return this.isLastRound() && this.isRoundDone() && this.getCurrentPhase() == Phase.ACTION;
    }
}
