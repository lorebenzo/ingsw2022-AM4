package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.AuthController;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.controller.game_state_controller.messages.*;
import it.polimi.ingsw.server.controller.game_state_controller.messages.enums.ReturnMessage;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import org.javatuples.Pair;

import java.util.List;
import java.util.UUID;

public class ExpertCommunicationController extends CommunicationController {
    protected ExpertCommunicationController(List<Player> players) throws GameStateInitializationFailureException {
        super(players, true);
    }

    public ExpertCommunicationController(List<Pair<String, Integer>> players, UUID gameUUID) {
        super(players, gameUUID);
    }

    @Override
    protected GameStateController initializeGameStateController(int playersNumber) throws GameStateInitializationFailureException {
        return new ExpertGameStateController(playersNumber);
    }

    @Override
    protected GameStateController initializeGameStateController(UUID gameUUID) {
        return new ExpertGameStateController(gameUUID);
    }

    /**
     * This is the handler of the messages for the characters with ID 2,4,6 and 8. It manages all the exceptions related to the rules of the game
     * and notifies the player with the result.
     * @param message is the CharacterIndexMsg received from the player
     * @param peer is the Peer from which the message is received
     * @return a SugarMessage with the result of the invocation
     */
    @SugarMessageHandler
    public SugarMessage characterIndexMsg(CharacterIndexMsg message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            this.gameStateController.applyEffect(message.characterIndex);
            return new OKAndUpdateMsg(new OKMsg(ReturnMessage.CHARACTER_PLAYED.text), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        } catch (InvalidCharacterIndexException e) {
            return new KOMsg(ReturnMessage.INVALID_CHARACTER_INDEX.text);
        } catch (MoveNotAvailableException e) {
            return new KOMsg(ReturnMessage.MOVE_NOT_AVAILABLE.text);
        } catch (WrongArgumentsException e) {
            return new KOMsg(ReturnMessage.WRONG_ARGUMENTS.text);
        } catch (NotEnoughCoinsException e) {
            return new KOMsg(ReturnMessage.NOT_ENOUGH_COINS.text);
        }
    }

    /**
     * This is the handler of the messages for the characters with ID 3 and 5. It manages all the exceptions related to the rules of the game and notifies the player with the result.
     * @param message is the CharacterIndexArchipelagoMsg received from the player
     * @param peer is the Peer from which the message is received
     * @return a SugarMessage with the result of the invocation
     */
    @SugarMessageHandler
    public SugarMessage characterIndexArchipelagoMsg(CharacterIndexArchipelagoMsg message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            boolean merged = this.gameStateController.applyEffect(message.characterIndex, message.archipelagoIslandCode);

            return new OKAndUpdateMsg(
                    new OKMsg(merged ? ReturnMessage.MERGE_PERFORMED.text : ReturnMessage.MERGE_NOT_PERFORMED.text),
                    new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId))
            );
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (NotEnoughCoinsException e) {
            return new KOMsg(ReturnMessage.NOT_ENOUGH_COINS.text);
        } catch (InvalidCharacterIndexException e) {
            return new KOMsg(ReturnMessage.INVALID_CHARACTER_INDEX.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        } catch (ArchipelagoAlreadyLockedException e) {
            return new KOMsg(ReturnMessage.ISLAND_ALREADY_LOCKED.text);
        } catch (InvalidArchipelagoIdException e) {
            return new KOMsg(ReturnMessage.INVALID_ARCHIPELAGO_ID.text);
        } catch (NoAvailableLockException e) {
            return new KOMsg(ReturnMessage.NO_AVAILABLE_LOCK.text);
        } catch (MoveNotAvailableException e) {
            return new KOMsg(ReturnMessage.MOVE_NOT_AVAILABLE.text);
        } catch (WrongArgumentsException e) {
            return new KOMsg(ReturnMessage.WRONG_ARGUMENTS.text);
        } catch (GameOverException e) {
            return new GameOverMsg(this.getUsernameToWinnerMap(e.schoolBoardIdToWinnerMap), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
        }
    }

    /**
     * This is the handler of the messages for the characters with ID 9, 11 and 12. It manages all the exceptions related to the rules of the game and notifies the player with the result.
     * @param message is the CharacterIndexColorMsg received from the player
     * @param peer is the Peer from which the message is received
     * @return a SugarMessage with the result of the invocation
     */
    @SugarMessageHandler
    public SugarMessage characterIndexColorMsg(CharacterIndexColorMsg message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            this.gameStateController.applyEffect(message.characterIndex, message.color);
            return new OKAndUpdateMsg(new OKMsg(ReturnMessage.CHARACTER_PLAYED.text), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        } catch (InvalidCharacterIndexException e) {
            return new KOMsg(ReturnMessage.INVALID_CHARACTER_INDEX.text);
        } catch (MoveNotAvailableException e) {
            return new KOMsg(ReturnMessage.MOVE_NOT_AVAILABLE.text);
        } catch (StudentNotOnCharacterException e) {
            return new KOMsg(ReturnMessage.STUDENT_NOT_ON_CHARACTER.text);
        } catch (FullDiningRoomLaneException e) {
            return new KOMsg(ReturnMessage.FULL_DINING_ROOM_LANE.text);
        } catch (WrongArgumentsException e) {
            return new KOMsg(ReturnMessage.WRONG_ARGUMENTS.text);
        } catch (NotEnoughCoinsException e) {
            return new KOMsg(ReturnMessage.NOT_ENOUGH_COINS.text);
        } catch (StudentsNotInTheDiningRoomException e) {
            return new KOMsg(ReturnMessage.STUDENTS_NOT_IN_THE_DINING_ROOM.text);
        }
    }

    /**
     * This is the handler of the messages for the characters with ID 1. It manages all the exceptions related to the rules of the game and notifies the player with the result.
     * @param message is the CharacterIndexColorArchipelagoMsg received from the player
     * @param peer is the Peer from which the message is received
     * @return a SugarMessage with the result of the invocation
     */
    @SugarMessageHandler
    public SugarMessage characterIndexColorArchipelagoMsg(CharacterIndexColorArchipelagoMsg message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            this.gameStateController.applyEffect(message.characterIndex, message.color, message.archipelagoIslandCode);
            return new OKAndUpdateMsg(new OKMsg(ReturnMessage.CHARACTER_PLAYED.text), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
        } catch (InvalidCharacterIndexException e) {
            return new KOMsg(ReturnMessage.INVALID_CHARACTER_INDEX.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (MoveNotAvailableException e) {
            return new KOMsg(ReturnMessage.MOVE_NOT_AVAILABLE.text);
        } catch (InvalidArchipelagoIdException e) {
            return new KOMsg(ReturnMessage.INVALID_ARCHIPELAGO_ID.text);
        } catch (StudentNotOnCharacterException e) {
            return new KOMsg(ReturnMessage.STUDENT_NOT_ON_CHARACTER.text);
        } catch (WrongArgumentsException e) {
            return new KOMsg(ReturnMessage.WRONG_ARGUMENTS.text);
        } catch (NotEnoughCoinsException e) {
            return new KOMsg(ReturnMessage.NOT_ENOUGH_COINS.text);
        }

    }

    /**
     * This is the handler of the messages for the characters with ID 1. It manages all the exceptions related to the rules of the game and notifies the player with the result.
     * @param message is the CharacterIndexColorListsMsg received from the player
     * @param peer is the Peer from which the message is received
     * @return a SugarMessage with the result of the invocation
     */
    @SugarMessageHandler
    public SugarMessage characterIndexColorListsMsg(CharacterIndexColorListsMsg message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            this.gameStateController.applyEffect(message.characterIndex, message.studentsToGet, message.studentsToGive);
            return new OKAndUpdateMsg(new OKMsg(ReturnMessage.CHARACTER_PLAYED.text), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
        } catch (InvalidCharacterIndexException e) {
            return new KOMsg(ReturnMessage.INVALID_CHARACTER_INDEX.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (WrongArgumentsException e) {
            return new KOMsg(ReturnMessage.WRONG_ARGUMENTS.text);
        } catch (InvalidStudentListsLengthException e) {
            return new KOMsg(ReturnMessage.INVALID_STUDENT_LISTS_LENGTH.text);
        } catch (StudentNotInTheEntranceException e) {
            return new KOMsg(ReturnMessage.STUDENT_NOT_IN_THE_ENTRANCE.text);
        } catch (StudentNotOnCharacterException e) {
            return new KOMsg(ReturnMessage.STUDENT_NOT_ON_CHARACTER.text);
        } catch (MoveNotAvailableException e) {
            return new KOMsg(ReturnMessage.MOVE_NOT_AVAILABLE.text);
        } catch (StudentsNotInTheDiningRoomException e) {
            return new KOMsg(ReturnMessage.STUDENTS_NOT_IN_THE_DINING_ROOM.text);
        } catch (FullDiningRoomLaneException e) {
            return new KOMsg(ReturnMessage.FULL_DINING_ROOM_LANE.text);
        } catch (NotEnoughCoinsException e) {
            return new KOMsg(ReturnMessage.NOT_ENOUGH_COINS.text);
        }
    }

    /**
     * This is the handler of the messages for the characters with ID 1. It manages all the exceptions related to the rules of the game and notifies the player with the result.
     * @param message is the RollbackMsg received from the player
     * @param peer is the Peer from which the message is received
     * @return a SugarMessage with the result of the invocation
     */
    @SugarMessageHandler
    @Override
    public SugarMessage rollbackMsg(RollbackMsg message, Peer peer) {
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        return new KOMsg("Rollback not available");
    }

    /**
     * This is the handler of the messages for the characters with ID 1. It manages all the exceptions related to the rules of the game and notifies the player with the result.
     * @param message is the EndTurnMsg received from the player
     * @param peer is the Peer from which the message is received
     * @return a SugarMessage with the result of the invocation
     */
    @SugarMessageHandler
    @Override
    public SugarMessage endTurnMsg(EndTurnMsg message, Peer peer) {
            var username = AuthController.getUsernameFromJWT(message.jwt);
            if(isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

            try {
                boolean lastRound = this.gameStateController.endActionTurn();

                return new OKAndUpdateMsg(
                        new OKMsg(lastRound ? ReturnMessage.TURN_ENDED.text + " " + ReturnMessage.LAST_ROUND.text: ReturnMessage.TURN_ENDED.text),
                        new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId))
                );
            } catch (MoreStudentsToBeMovedException e){
                return new KOMsg(ReturnMessage.MORE_STUDENTS_TO_BE_MOVED.text);
            } catch (MotherNatureToBeMovedException e){
                return new KOMsg(ReturnMessage.MOTHER_NATURE_TO_BE_MOVED.text);
            } catch (StudentsToBeGrabbedFromCloudException e){
                return new KOMsg(ReturnMessage.STUDENTS_TO_BE_GRABBED_FROM_CLOUD.text);
            } catch (CardNotPlayedException e){
                return new KOMsg(ReturnMessage.CARD_NOT_PLAYED.text);
            } catch (WrongPhaseException e) {
                return new KOMsg(ReturnMessage.WRONG_PHASE.text);
            } catch (GameOverException e) {
                return new GameOverMsg(this.getUsernameToWinnerMap(e.schoolBoardIdToWinnerMap), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
            }

    }
}