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

import java.util.List;

public class ExpertCommunicationController extends CommunicationController {
    protected ExpertCommunicationController(List<Player> players) throws GameStateInitializationFailureException {
        super(players);
    }

    @Override
    protected GameStateController initializeGameStateController(int playersNumber) throws GameStateInitializationFailureException {
        return new ExpertGameStateController(playersNumber);
    }

    //Handler that manages characterIds 2,4,6,8
    @SugarMessageHandler
    public SugarMessage characterIndexMsg(SugarMessage message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (CharacterIndexMsg)message;

        try {
            this.gameStateController.applyEffect(msg.characterIndex);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
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

    //Handler that manages characterIds 3 and 5 - specifically conquer archipelago of choice and lock archipelago
    @SugarMessageHandler
    public SugarMessage characterIndexArchipelagoMsg(SugarMessage message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (CharacterIndexArchipelagoMsg) message;
        try {
            boolean merged = this.gameStateController.applyEffect(msg.characterIndex, msg.archipelagoIslandCode);

            return new OKAndUpdateMsg(
                    new OKMsg(merged ? ReturnMessage.MERGE_PERFORMED.text : ReturnMessage.MERGE_NOT_PERFORMED.text),
                    new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID))
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
        }
    }

    //Handler that manages characterIds 9,11,12
    @SugarMessageHandler
    public SugarMessage characterIndexColorMsg(SugarMessage message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (CharacterIndexColorMsg)message;

        try {
            this.gameStateController.applyEffect(msg.characterIndex, msg.color);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
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

    @SugarMessageHandler
    public SugarMessage characterIndexColorArchipelagoMsg(SugarMessage message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (CharacterIndexColorArchipelagoMsg)message;

        try {
            this.gameStateController.applyEffect(msg.characterIndex, msg.color, msg.archipelagoIslandCode);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
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

    @SugarMessageHandler
    public SugarMessage characterIndexColorListsMsg(SugarMessage message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (CharacterIndexColorListsMsg)message;

        try {
            this.gameStateController.applyEffect(msg.characterIndex, msg.studentsToGet, msg.studentsToGive);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
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

}