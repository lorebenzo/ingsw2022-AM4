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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommunicationController extends SugarMessageProcessor{

    protected GameStateController gameStateController;
    protected Map<Peer,Integer> peersToSchoolBoardIdsMap;

    protected CommunicationController(List<Peer> peers) throws GameStateInitializationFailureException, EmptyStudentSupplyException {

        this.initializeGameStateController(peers.size());
        this.initializePeerToSchoolBoardIdMap(peers);

    }

    protected void initializeGameStateController(int playersNumber) throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        this.gameStateController = new GameStateController(playersNumber);
    }

    protected void initializePeerToSchoolBoardIdMap(List<Peer> peers){
        this.peersToSchoolBoardIdsMap = new HashMap<>();

        //Create a map that links every peer to a schoolBoardId
        Iterator<Integer> schoolBoardIdsSetIterator = this.gameStateController.getSchoolBoardIds().iterator();

        for (Peer peer : peers) {
            this.peersToSchoolBoardIdsMap.put(peer, schoolBoardIdsSetIterator.next());
        }
    }



    public static CommunicationController createCommunicationController(List<Peer> peers, boolean isExpertMode) throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        if(isExpertMode)
            return new ExpertCommunicationController(peers);
        else
            return new CommunicationController(peers);
    }



    private Peer getPeerFromSchoolBoardId(int schoolBoardId){
        return this.peersToSchoolBoardIdsMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == schoolBoardId)
                .map(Map.Entry::getKey).toList()
                .get(0);
    }

    private int getSchoolBoardIdFromPeer(Peer peer){
        return this.peersToSchoolBoardIdsMap.get(peer);
    }


    //Necessary to CommunicationController

    /**
     * This method whether a move was sent by the current player or not.
     * @param peer indicates a  peer, therefore a player,
     * @return true if the move has to be processed, because it is performed by the current player, false otherwise.
     */
    boolean isOthersPlayerTurn(Peer peer){
        return this.getSchoolBoardIdFromPeer(peer) != this.gameStateController.getCurrentPlayerSchoolBoardId();
    }

    @SugarMessageHandler
    public SugarMessage playCardMsg(SugarMessage message, Peer peer){

        if(this.isOthersPlayerTurn(peer)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (PlayCardMsg) message;

        try {
            this.gameStateController.playCard(msg.card);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState()));
        } catch (WrongPhaseException e){
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (CardIsNotInTheDeckException e) {
            return new KOMsg(ReturnMessage.CARD_IS_NOT_IN_THE_DECK.text);
        }catch (InvalidCardPlayedException e) {
            return new KOMsg(ReturnMessage.INVALID_CARD_PLAYED.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        }
    }

    @SugarMessageHandler
    public SugarMessage moveStudentFromEntranceToDiningRoomMsg(SugarMessage message, Peer peer){
        if(this.isOthersPlayerTurn(peer)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (MoveStudentFromEntranceToDiningRoomMsg) message;

        try {
            this.gameStateController.moveStudentFromEntranceToDiningRoom(msg.student);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState()));
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (StudentNotInTheEntranceException e) {
            return new KOMsg(ReturnMessage.STUDENT_NOT_IN_THE_ENTRANCE.text);
        } catch (FullDiningRoomLaneException e) {
            return new KOMsg(ReturnMessage.FULL_DINING_ROOM_LANE.text);
        } catch (TooManyStudentsMovedException e) {
            return new KOMsg(ReturnMessage.TOO_MANY_STUDENTS_MOVED.text);
        }

    }

    @SugarMessageHandler
    public SugarMessage moveStudentFromEntranceToArchipelagoMsg(SugarMessage message, Peer peer) {
        if (this.isOthersPlayerTurn(peer)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (MoveStudentFromEntranceToArchipelagoMsg) message;

        try {
            this.gameStateController.moveStudentFromEntranceToArchipelago(msg.student, msg.archipelagoIslandCodes);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState()));
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (StudentNotInTheEntranceException e) {
            return new KOMsg(ReturnMessage.STUDENT_NOT_IN_THE_ENTRANCE.text);
        } catch (TooManyStudentsMovedException e) {
            return new KOMsg(ReturnMessage.TOO_MANY_STUDENTS_MOVED.text);
        }

    }


    @SugarMessageHandler
    public SugarMessage moveMotherNatureMsg(SugarMessage message, Peer peer) {
        if(this.isOthersPlayerTurn(peer)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (MoveMotherNatureMsg) message;

        try {
            boolean merged = this.gameStateController.moveMotherNature(msg.numberOfSteps);

            // Check winners
            Map<Integer, Boolean> winners = this.gameStateController.checkWinners(false);
            if(winners.containsValue(true) /* someone won */) {
                // perform map composition: (peer->schoolBoard) ° (schoolBoard->isWinner) = (peer->isWinner)
                Map<Peer, Boolean> peerToIsWinner = new HashMap<>();
                for(var _peer : this.peersToSchoolBoardIdsMap.keySet())
                    peerToIsWinner.put(_peer, winners.get(this.peersToSchoolBoardIdsMap.get(_peer)));
                return new GameOverMsg(peerToIsWinner);
            }

            if(merged) {
                return new OKAndUpdateMsg(new OKMsg(ReturnMessage.MERGE_PERFORMED.text), new UpdateClientMsg(this.gameStateController.getLightGameState()));
            }
            else
                return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState()));
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (InvalidNumberOfStepsException e) {
            return new KOMsg(ReturnMessage.INVALID_NUMBER_OF_STEPS.text);
        } catch (MoreStudentsToBeMovedException e) {
            return new KOMsg(ReturnMessage.MORE_STUDENTS_TO_BE_MOVED.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        }
    }

    @SugarMessageHandler
    public SugarMessage grabStudentsFromCloudMsg(SugarMessage message, Peer peer){
        if(this.isOthersPlayerTurn(peer)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (GrabStudentsFromCloudMsg) message;

        try {
            this.gameStateController.grabStudentsFromCloud(msg.cloudIndex);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState()));
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (EmptyCloudException e) {
            return new KOMsg(ReturnMessage.EMPTY_CLOUD.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        } catch (MotherNatureToBeMovedException e) {
            return new KOMsg(ReturnMessage.MOTHER_NATURE_TO_BE_MOVED.text);
        }
    }


    @SugarMessageHandler
    public SugarMessage endTurnMsg(SugarMessage message, Peer peer){

        if(this.isOthersPlayerTurn(peer)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            this.gameStateController.endActionTurn();

            // Check winners
            var winners = this.gameStateController.checkWinners(false);
            if(winners.containsValue(true) /* someone won */) {
                // perform map composition: (peer->schoolBoard) ° (schoolBoard->isWinner) = (peer->isWinner)
                Map<Peer, Boolean> peerToIsWinner = new HashMap<>();
                for(var _peer : this.peersToSchoolBoardIdsMap.keySet())
                    peerToIsWinner.put(_peer, winners.get(this.peersToSchoolBoardIdsMap.get(_peer)));
                return new GameOverMsg(peerToIsWinner);
            }
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
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        }
        return new OKMsg();
    }


    @SugarMessageHandler
    public void base(SugarMessage message, Peer peer) throws UnhandledMessageAtLowestLayerException {
        System.out.println("Dropping message : " + message.serialize());
        throw new UnhandledMessageAtLowestLayerException(message);
    }

}
