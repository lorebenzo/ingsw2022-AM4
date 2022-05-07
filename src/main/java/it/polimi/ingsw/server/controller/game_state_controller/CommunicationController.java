package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.exceptions.UnhandledMessageAtLowestLayerException;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.controller.game_state_controller.messages.*;
import it.polimi.ingsw.server.controller.game_state_controller.messages.enums.ReturnMessage;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.List;

public class CommunicationController extends SugarMessageProcessor {

    private final GameStateController gameStateController;

    public CommunicationController(List<Peer> peers) throws GameStateInitializationFailureException, EmptyStudentSupplyException {

        this.gameStateController = new GameStateController(peers);
    }


    @SugarMessageHandler
    public SugarMessage playCardMsg(SugarMessage message, Peer peer){
        if(this.gameStateController.isMoveFromCurrentPlayer(peer)){
            var msg = (PlayCardMsg) message;

            try {
                this.gameStateController.playCard(msg.card);
                return new OKMsg();
            } catch (WrongPhaseException e){
                return new KOMsg(ReturnMessage.WRONG_PHASE_EXCEPTION.text);

            } catch (CardIsNotInTheDeckException e) {
                return new KOMsg(ReturnMessage.CARD_IS_NOT_IN_THE_DECK.text);

            }catch (InvalidCardPlayedException e) {
                return new KOMsg(ReturnMessage.INVALID_CARD_PLAYED.text);
            } catch (MoveAlreadyPlayedException e) {
                return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
            }
        }
        else
            return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);


    }

    @SugarMessageHandler
    public SugarMessage moveStudentFromEntranceToDiningRoomMsg(SugarMessage message, Peer peer){
        if(this.gameStateController.isMoveFromCurrentPlayer(peer)){
            var msg = (MoveStudentFromEntranceToDiningRoomMsg) message;

            try {
                this.gameStateController.moveStudentFromEntranceToDiningRoom(msg.student);
                return new OKMsg();
            } catch (WrongPhaseException e) {
                return new KOMsg(ReturnMessage.WRONG_PHASE_EXCEPTION.text);
            } catch (StudentNotInTheEntranceException e) {
                return new KOMsg(ReturnMessage.STUDENT_NOT_IN_THE_ENTRANCE.text);
            } catch (FullDiningRoomLaneException e) {
                return new KOMsg(ReturnMessage.FULL_DINING_ROOM_LANE.text);
            } catch (TooManyStudentsMovedException e) {
                return new KOMsg(ReturnMessage.TOO_MANY_STUDENTS_MOVED.text);
            }

        }
        else
            return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);
    }

    @SugarMessageHandler
    public SugarMessage moveStudentFromEntranceToArchipelagoMsg(SugarMessage message, Peer peer) {
        if (this.gameStateController.isMoveFromCurrentPlayer(peer)) {
            var msg = (MoveStudentFromEntranceToArchipelagoMsg) message;

            try {
                this.gameStateController.moveStudentFromEntranceToArchipelago(msg.student, msg.archipelagoIslandCodes);
                return new OKMsg();
            } catch (WrongPhaseException e) {
                return new KOMsg(ReturnMessage.WRONG_PHASE_EXCEPTION.text);
            } catch (StudentNotInTheEntranceException e) {
                return new KOMsg(ReturnMessage.STUDENT_NOT_IN_THE_ENTRANCE.text);
            } catch (TooManyStudentsMovedException e) {
                return new KOMsg(ReturnMessage.TOO_MANY_STUDENTS_MOVED.text);
            }
        }
        else
            return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);
    }


    @SugarMessageHandler
    public SugarMessage moveMotherNatureMsg(SugarMessage message, Peer peer) {
        if(this.gameStateController.isMoveFromCurrentPlayer(peer)){
            var msg = (MoveMotherNatureMsg) message;

            try {
                if(this.gameStateController.moveMotherNature(msg.numberOfSteps))
                    return new OKMsg(ReturnMessage.MERGE_PERFORMED.text);
                else
                    return new OKMsg();
            } catch (WrongPhaseException e) {
                return new KOMsg(ReturnMessage.WRONG_PHASE_EXCEPTION.text);
            } catch (InvalidNumberOfStepsException e) {
                return new KOMsg(ReturnMessage.INVALID_NUMBER_OF_STEPS.text);
            } catch (MoreStudentsToBeMovedException e) {
                return new KOMsg(ReturnMessage.MORE_STUDENTS_TO_BE_MOVED.text);
            } catch (MoveAlreadyPlayedException e) {
                return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
            }
        }
        else
            return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);
    }

    @SugarMessageHandler
    public SugarMessage grabStudentsFromCloudMsg(SugarMessage message, Peer peer){
        if(this.gameStateController.isMoveFromCurrentPlayer(peer)){
            var msg = (GrabStudentsFromCloudMsg) message;

            try {
                this.gameStateController.grabStudentsFromCloud(msg.cloudIndex);
                return new OKMsg();
            } catch (WrongPhaseException e) {
                return new KOMsg(ReturnMessage.WRONG_PHASE_EXCEPTION.text);
            } catch (EmptyCloudException e) {
                return new KOMsg(ReturnMessage.EMPTY_CLOUD.text);
            } catch (MoveAlreadyPlayedException e) {
                return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
            } catch (MotherNatureToBeMovedException e) {
                return new KOMsg(ReturnMessage.MOTHER_NATURE_TO_BE_MOVED.text);
            }
        }
        else
            return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);
    }


    @SugarMessageHandler
    public SugarMessage endTurnMsg(SugarMessage message, Peer peer){
        if(this.gameStateController.isMoveFromCurrentPlayer(peer)){
            try {
                this.gameStateController.endActionTurn();
            } catch (GameOverException e) {
                return new GameOverMsg();
            } catch (MoreStudentsToBeMovedException e){
                return new KOMsg(ReturnMessage.MORE_STUDENTS_TO_BE_MOVED.text);
            } catch (MotherNatureToBeMovedException e){
                return new KOMsg(ReturnMessage.MOTHER_NATURE_TO_BE_MOVED.text);
            } catch (StudentsToBeGrabbedFromCloudException e){
                return new KOMsg(ReturnMessage.STUDENTS_TO_BE_GRABBED_FROM_CLOUD.text);
            } catch (CardNotPlayedException e){
                return new KOMsg(ReturnMessage.CARD_NOT_PLAYED.text);
            } catch (EmptyStudentSupplyException e) {
                return new KOMsg("Empty student supply"); //TODO transform this message in a GameOver condition
            } catch (WrongPhaseException e) {
                return new KOMsg(ReturnMessage.WRONG_PHASE_EXCEPTION.text);
            }

            return new OKMsg();
        }
        else
            return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);
    }


    @SugarMessageHandler
    public void base(SugarMessage message, Peer peer) throws UnhandledMessageAtLowestLayerException {
        System.out.println("Dropping message : " + message.serialize());
        throw new UnhandledMessageAtLowestLayerException(message);
    }
}
