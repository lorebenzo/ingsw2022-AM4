package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.MoveAlreadyPlayedException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.MoveNotAvailableException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.WrongPhaseException;
import it.polimi.ingsw.server.controller.game_state_controller.messages.*;
import it.polimi.ingsw.server.controller.game_state_controller.messages.enums.ReturnMessage;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpertCommunicationController extends CommunicationController {
    protected ExpertCommunicationController(List<Peer> peers) throws GameStateInitializationFailureException {
        super(peers);
    }

    @Override
    protected void initializeGameStateController(int playersNumber) throws GameStateInitializationFailureException {
        this.gameStateController = new ExpertGameStateController(playersNumber);
    }

    //Handler that manages characterIds 3 and 5 - specifically conquer archipelago of choice and lock archipelago
    @SugarMessageHandler
    public SugarMessage characterIdIslandMsg(SugarMessage message, Peer peer){
        if(!this.isMoveFromCurrentPlayer(peer)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (CharacterIdIslandMsg) message;
        try {
            boolean merged = this.gameStateController.applyEffect(msg.characterId, msg.archipelagoIslandCode);
            // Check winners
            var winners = this.gameStateController.checkWinners(false);
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
        }

    }

    //Handler that manages characterIds 2,4,6,8
    @SugarMessageHandler
    public SugarMessage characterIdMsg(SugarMessage message, Peer peer){
        if(!isMoveFromCurrentPlayer(peer)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (CharacterIdMsg)message;

        try {
            this.gameStateController.applyEffect(msg.characterId);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState()));
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        } catch (InvalidCharacterIndexException e) {
            return new KOMsg(ReturnMessage.INVALID_CHARACTER_INDEX.text);
        } catch (MoveNotAvailableException e) {
            return new KOMsg(ReturnMessage.MOVE_NOT_AVAILABLE.text);
        }


    }

}