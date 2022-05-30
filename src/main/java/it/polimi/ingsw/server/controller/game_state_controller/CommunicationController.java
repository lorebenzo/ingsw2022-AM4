package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.exceptions.UnhandledMessageAtLowestLayerException;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.AuthController;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.controller.game_state_controller.messages.*;
import it.polimi.ingsw.server.controller.game_state_controller.messages.enums.ReturnMessage;
import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.*;

public class CommunicationController extends SugarMessageProcessor {

    private final GameStateController gameStateController;
    private Map<String, Integer> usernameToSchoolBoardID;

    public CommunicationController(List<Player> players) {

        GameStateController gameStateController1;
        try {
            gameStateController1 = new GameStateController(players.size());
        } catch(GameStateInitializationFailureException e) {
            // Something went wrong, everything breaks
            gameStateController1 = null;
            e.printStackTrace();
        }
        this.gameStateController = gameStateController1;


        this.usernameToSchoolBoardID = new HashMap<>();

        //Create a map that links every peer to a schoolBoardId
        Iterator<Integer> schoolBoardIdsSetIterator = this.gameStateController.getSchoolBoardIds().iterator();

        for (var player : players) {
            this.usernameToSchoolBoardID.put(player.username, schoolBoardIdsSetIterator.next());
        }
    }

//    private Peer getPeerFromSchoolBoardId(int schoolBoardId){
//        return this.playersToSchoolBoardIdsMap.entrySet()
//                .stream()
//                .filter(entry -> entry.getValue() == schoolBoardId)
//                .map(Map.Entry::getKey).toList()
//                .get(0)
//                .associatedPeer;
//    }

    private int getSchoolBoardIdFromPeer(String player){
        return this.usernameToSchoolBoardID.get(player);
    }


    //Necessary to CommunicationController

    /**
     * This method whether a move was sent by the current player or not.
     * @param player indicates a  peer, therefore a player,
     * @return true if the move has to be processed, because it is performed by the current player, false otherwise.
     */
    boolean isMoveFromCurrentPlayer(String player){
        return this.getSchoolBoardIdFromPeer(player) == this.gameStateController.getCurrentPlayerSchoolBoardId();
    }

    @SugarMessageHandler
    public SugarMessage playCardMsg(SugarMessage message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isMoveFromCurrentPlayer(username)){
            System.out.println("It's your turn");
            var msg = (PlayCardMsg) message;

            try {
                this.gameStateController.playCard(msg.card);
                return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
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
        else {
            System.out.println("NOT YOUR TURN");
            return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);
        }
    }

    @SugarMessageHandler
    public SugarMessage moveStudentFromEntranceToDiningRoomMsg(SugarMessage message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isMoveFromCurrentPlayer(username)){
            var msg = (MoveStudentFromEntranceToDiningRoomMsg) message;

            try {
                this.gameStateController.moveStudentFromEntranceToDiningRoom(msg.student);
                return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
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
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if (this.isMoveFromCurrentPlayer(username)) {
            var msg = (MoveStudentFromEntranceToArchipelagoMsg) message;

            try {
                var archipelagoIslandCodes = this.gameStateController.getLightGameState().archipelagos
                        .stream()
                        .map(Archipelago::getIslandCodes)
                        .filter(ic -> ic.contains(msg.archipelagoIslandCode))
                        .findFirst()
                        .orElseThrow(InvalidArchipelagoIdException::new);
                this.gameStateController.moveStudentFromEntranceToArchipelago(msg.student, archipelagoIslandCodes);
                return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
            } catch (WrongPhaseException e) {
                return new KOMsg(ReturnMessage.WRONG_PHASE_EXCEPTION.text);
            } catch (StudentNotInTheEntranceException e) {
                return new KOMsg(ReturnMessage.STUDENT_NOT_IN_THE_ENTRANCE.text);
            } catch (TooManyStudentsMovedException e) {
                return new KOMsg(ReturnMessage.TOO_MANY_STUDENTS_MOVED.text);
            } catch (InvalidArchipelagoIdException e) {
                return new KOMsg(ReturnMessage.INVALID_ARCHIPELAGO_ID.text);
            }
        }
        else
            return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);
    }


    @SugarMessageHandler
    public SugarMessage moveMotherNatureMsg(SugarMessage message, Peer peer) {
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isMoveFromCurrentPlayer(username)){
            var msg = (MoveMotherNatureMsg) message;

            try {
                boolean merged = this.gameStateController.moveMotherNature(msg.numberOfSteps);

                // Check winners
                Optional<Map<Integer, Boolean>> schoolBoardIdToIsWinnerMap = this.gameStateController.checkImmediateWinners();

                //If someone won
                if(schoolBoardIdToIsWinnerMap.isPresent()) {
                    // perform map composition: (peer->schoolBoard) ° (schoolBoard->isWinner) = (peer->isWinner)
                    Map<String, Boolean> peerToIsWinner = new HashMap<>();

                    for(var _peer : this.usernameToSchoolBoardID.keySet())
                        peerToIsWinner.put(_peer, schoolBoardIdToIsWinnerMap.get().get(this.usernameToSchoolBoardID.get(_peer)));
                    return new GameOverMsg(peerToIsWinner, new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
                }

                return new OKAndUpdateMsg(
                        new OKMsg(merged ? ReturnMessage.MERGE_PERFORMED.text : ReturnMessage.MERGE_NOT_PERFORMED.text),
                        new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID))
                );
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
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isMoveFromCurrentPlayer(username)){
            var msg = (GrabStudentsFromCloudMsg) message;

            try {
                this.gameStateController.grabStudentsFromCloud(msg.cloudIndex);
                return new OKAndUpdateMsg(
                        new OKMsg(ReturnMessage.STUDENTS_GRABBED_FROM_CLOUD.text),
                        new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID))
                );
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
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isMoveFromCurrentPlayer(username)){
            try {
                this.gameStateController.endActionTurn();
                return new OKAndUpdateMsg(new OKMsg(ReturnMessage.TURN_ENDED.text), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
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
        }
        else
            return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);
    }

    public LightGameState getLightGameState() {
        return this.gameStateController.getLightGameState().addUsernames(usernameToSchoolBoardID);
    }


    @SugarMessageHandler
    public void base(SugarMessage message, Peer peer) throws UnhandledMessageAtLowestLayerException {
        System.out.println("Dropping message : " + message.serialize());
        throw new UnhandledMessageAtLowestLayerException(message);
    }
}
