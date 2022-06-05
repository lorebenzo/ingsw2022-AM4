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

    protected GameStateController gameStateController;
    protected final Map<String, Integer> usernameToSchoolBoardID;

    public CommunicationController(List<Player> players) throws GameStateInitializationFailureException {
        this.gameStateController = initializeGameStateController(players.size());
        this.usernameToSchoolBoardID = new HashMap<>();

        //Create a map that links every peer to a schoolBoardId
        Iterator<Integer> schoolBoardIdsSetIterator = this.gameStateController.getSchoolBoardIds().iterator();

        for (var player : players) {
            this.usernameToSchoolBoardID.put(player.username, schoolBoardIdsSetIterator.next());
        }
    }



    public static CommunicationController createCommunicationController(List<Player> players, boolean isExpertMode) throws GameStateInitializationFailureException {
        if(isExpertMode)
            return new ExpertCommunicationController(players);
        else
            return new CommunicationController(players);
    }

    private int getSchoolBoardIdFromUsername(String player){
        return this.usernameToSchoolBoardID.get(player);
    }

    private String getUsernameFromSchoolBoardId(int schoolBoardId){
        return this.usernameToSchoolBoardID.entrySet().stream().filter(entry -> entry.getValue() == schoolBoardId).map(Map.Entry::getKey).findFirst().get();
    }

    //Necessary to CommunicationController

    /**
     * This method whether a move was sent by the current player or not.
     * @param player indicates a  peer, therefore a player,
     * @return true if the move has to be processed, because it is performed by the current player, false otherwise.
     */
    boolean isOthersPlayersTurn(String player){
        return this.getSchoolBoardIdFromUsername(player) != this.gameStateController.getCurrentPlayerSchoolBoardId();
    }

    @SugarMessageHandler
    public SugarMessage playCardMsg(SugarMessage message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (PlayCardMsg) message;

        try {
            this.gameStateController.playCard(msg.card);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
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
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);
        var msg = (MoveStudentFromEntranceToDiningRoomMsg) message;

        try {
            this.gameStateController.moveStudentFromEntranceToDiningRoom(msg.student);
            return new OKAndUpdateMsg(new OKMsg(), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
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
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

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
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (StudentNotInTheEntranceException e) {
            return new KOMsg(ReturnMessage.STUDENT_NOT_IN_THE_ENTRANCE.text);
        } catch (TooManyStudentsMovedException e) {
            return new KOMsg(ReturnMessage.TOO_MANY_STUDENTS_MOVED.text);
        } catch (InvalidArchipelagoIdException e) {
            return new KOMsg(ReturnMessage.INVALID_ARCHIPELAGO_ID.text);
        }
    }

    protected GameStateController initializeGameStateController(int playersNumber) throws GameStateInitializationFailureException {
        return new GameStateController(playersNumber);
    }


    @SugarMessageHandler
    public SugarMessage moveMotherNatureMsg(SugarMessage message, Peer peer) {
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (MoveMotherNatureMsg) message;

        try {
            boolean merged = this.gameStateController.moveMotherNature(msg.numberOfSteps);

            return new OKAndUpdateMsg(
                    new OKMsg(merged ? ReturnMessage.MERGE_PERFORMED.text : ReturnMessage.MERGE_NOT_PERFORMED.text),
                    new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID))
            );
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (InvalidNumberOfStepsException e) {
            return new KOMsg(ReturnMessage.INVALID_NUMBER_OF_STEPS.text);
        } catch (MoreStudentsToBeMovedException e) {
            return new KOMsg(ReturnMessage.MORE_STUDENTS_TO_BE_MOVED.text);
        } catch (MoveAlreadyPlayedException e) {
            return new KOMsg(ReturnMessage.MOVE_ALREADY_PLAYED.text);
        } catch (GameOverException e) {
            return new GameOverMsg(this.getUsernameToWinnerMap(e.schoolBoardIdToWinnerMap), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
        }
    }

    @SugarMessageHandler
    public SugarMessage grabStudentsFromCloudMsg(SugarMessage message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        var msg = (GrabStudentsFromCloudMsg) message;

        try {
            this.gameStateController.grabStudentsFromCloud(msg.cloudIndex);
            return new OKAndUpdateMsg(
                    new OKMsg(ReturnMessage.STUDENTS_GRABBED_FROM_CLOUD.text),
                    new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID))
            );
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
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            this.gameStateController.endActionTurn();
        } catch (MoreStudentsToBeMovedException e){
            return new KOMsg(ReturnMessage.MORE_STUDENTS_TO_BE_MOVED.text);
        } catch (MotherNatureToBeMovedException e){
            return new KOMsg(ReturnMessage.MOTHER_NATURE_TO_BE_MOVED.text);
        } catch (StudentsToBeGrabbedFromCloudException e){
            return new KOMsg(ReturnMessage.STUDENTS_TO_BE_GRABBED_FROM_CLOUD.text);
        } catch (CardNotPlayedException e){
            return new KOMsg(ReturnMessage.CARD_NOT_PLAYED.text);
        } catch (LastRoundException e) {
            return new OKAndUpdateMsg(
                    new OKMsg(ReturnMessage.LAST_ROUND.text),
                    new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID))
            );
        } catch (WrongPhaseException e) {
            return new KOMsg(ReturnMessage.WRONG_PHASE.text);
        } catch (GameOverException e) {
            return new GameOverMsg(this.getUsernameToWinnerMap(e.schoolBoardIdToWinnerMap), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID)));
        }
        return new OKAndUpdateMsg(
                new OKMsg(ReturnMessage.STUDENTS_GRABBED_FROM_CLOUD.text),
                new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardID))
        );
    }

    private Map<String, Boolean> getUsernameToWinnerMap(Map<Integer, Boolean> schoolBoardIdToWinnerMap){
        Map<String, Boolean> usernameToWinnerMap = new HashMap<>();

        for (int schoolBoardId: schoolBoardIdToWinnerMap.keySet()) {
            usernameToWinnerMap.put(this.getUsernameFromSchoolBoardId(schoolBoardId), schoolBoardIdToWinnerMap.get(schoolBoardId));
        }
        return usernameToWinnerMap;
    }


    /**
     * Returns the usernames in my team
     * @param username of the player
     * @return a List<String> that contains all the users in my team, in 2/3 players games,
     *         it returns a list containing only the given username
     */
    public List<String> getTeamUsernames(String username) {
        var userSchoolBoardId = usernameToSchoolBoardID.get(username);
        var userTowerColor = this.gameStateController.getSchoolBoardTowerColorFromID(userSchoolBoardId);
        var teamSchoolBoardIDs = this.gameStateController.getSchoolBoardIDFromTowerColor(userTowerColor);

        List<String> teamUsernames = new LinkedList<>();

        this.usernameToSchoolBoardID.forEach((user, sbID) -> {
            if(teamSchoolBoardIDs.contains(sbID)) teamUsernames.add(user);
        });

        return teamUsernames;
    }

    /**
     * Return light game state from the communication controller
     * @return light game state
     */
    public LightGameState getLightGameState() {
        return this.gameStateController.getLightGameState().addUsernames(usernameToSchoolBoardID);
    }


    @SugarMessageHandler
    public void base(SugarMessage message, Peer peer) throws UnhandledMessageAtLowestLayerException {
        System.out.println("Dropping message : " + message.serialize());
        throw new UnhandledMessageAtLowestLayerException(message);
    }
}
