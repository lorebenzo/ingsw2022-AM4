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
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.entities.Player;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import it.polimi.ingsw.server.repository.GamesRepository;
import org.javatuples.Pair;

import java.util.*;

public class CommunicationController extends SugarMessageProcessor {

    protected GameStateController gameStateController;
    protected final Map<String, Integer> usernameToSchoolBoardId;

    protected CommunicationController(List<Player> players) throws GameStateInitializationFailureException {
        this.gameStateController = initializeGameStateController(players.size());
        this.usernameToSchoolBoardId = new HashMap<>();

        //Create a map that links every peer to a schoolBoardId
        Iterator<Integer> schoolBoardIdsSetIterator = this.gameStateController.getSchoolBoardIds().iterator();

        for (var player : players) {
            this.usernameToSchoolBoardId.put(player.username, schoolBoardIdsSetIterator.next());
        }

        var gameUUID = gameStateController.getGameUUID();
        for (int i = 0; i < players.size(); i++) {
            GamesRepository.getInstance().saveUserSchoolBoardMap(gameUUID, players.get(i).username, i);
        }
    }

    public CommunicationController(List<Pair<String, Integer>> players, UUID gameUUID) {
        this.gameStateController = initializeGameStateController(gameUUID);
        this.usernameToSchoolBoardId = new HashMap<>();

        players.forEach(player -> this.usernameToSchoolBoardId.put(player.getValue0(), player.getValue1()));
    }

    protected GameStateController initializeGameStateController(int playersNumber) throws GameStateInitializationFailureException {
        return new GameStateController(playersNumber);
    }

    public static CommunicationController createCommunicationController(List<Player> players, boolean isExpertMode) throws GameStateInitializationFailureException {
        if(isExpertMode)
            return new ExpertCommunicationController(players);
        else
            return new CommunicationController(players);
    }

    public static CommunicationController createCommunicationController(List<Pair<String, Integer>> players, boolean isExpertMode, UUID gameUUID) {
        if(isExpertMode)
            return new ExpertCommunicationController(players, gameUUID);
        else
            return new CommunicationController(players, gameUUID);
    }

    private int getSchoolBoardIdFromPeer(String player){
        return this.usernameToSchoolBoardId.get(player);
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
    public SugarMessage rollbackMsg(RollbackMsg message, Peer peer) {
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        this.gameStateController.rollback();

        return new OKAndUpdateMsg(new OKMsg(ReturnMessage.ROLLBACK.text), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));

    }

    @SugarMessageHandler
    public SugarMessage playCardMsg(PlayCardMsg message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            boolean lastRound = this.gameStateController.playCard(message.card);
            return new OKAndUpdateMsg(new OKMsg(lastRound ? ReturnMessage.CARD_PLAYED.text + " " + ReturnMessage.NO_MORE_CARDS.text: ReturnMessage.CARD_PLAYED.text), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
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
    public SugarMessage moveStudentFromEntranceToDiningRoomMsg(MoveStudentFromEntranceToDiningRoomMsg message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            this.gameStateController.moveStudentFromEntranceToDiningRoom(message.student);
            return new OKAndUpdateMsg(new OKMsg(ReturnMessage.STUDENT_MOVED_DINING.text), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
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
    public SugarMessage moveStudentFromEntranceToArchipelagoMsg(MoveStudentFromEntranceToArchipelagoMsg message, Peer peer) {
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            var archipelagoIslandCodes = this.gameStateController.getLightGameState().archipelagos
                    .stream()
                    .map(lightArchipelago -> lightArchipelago.islandCodes)
                    .filter(ic -> ic.contains(message.archipelagoIslandCode))
                    .findFirst()
                    .orElseThrow(InvalidArchipelagoIdException::new);
            this.gameStateController.moveStudentFromEntranceToArchipelago(message.student, archipelagoIslandCodes);
            return new OKAndUpdateMsg(new OKMsg(ReturnMessage.STUDENT_MOVED_DINING.text), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
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

    protected GameStateController initializeGameStateController(UUID gameUUID) {
        return new GameStateController(gameUUID);
    }


    @SugarMessageHandler
    public SugarMessage moveMotherNatureMsg(MoveMotherNatureMsg message, Peer peer) {
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            boolean merged = this.gameStateController.moveMotherNature(message.numberOfSteps);

            return new OKAndUpdateMsg(
                    new OKMsg(merged ? ReturnMessage.MERGE_PERFORMED.text : ReturnMessage.MERGE_NOT_PERFORMED.text),
                    new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId))
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
            return new GameOverMsg(this.getUsernameToWinnerMap(e.schoolBoardIdToWinnerMap), new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId)));
        }
    }

    @SugarMessageHandler
    public SugarMessage grabStudentsFromCloudMsg(GrabStudentsFromCloudMsg message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(this.isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            this.gameStateController.grabStudentsFromCloud(message.cloudIndex);
            return new OKAndUpdateMsg(
                    new OKMsg(ReturnMessage.STUDENTS_GRABBED_FROM_CLOUD.text),
                    new UpdateClientMsg(this.gameStateController.getLightGameState().addUsernames(this.usernameToSchoolBoardId))
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
    public SugarMessage endTurnMsg(EndTurnMsg message, Peer peer){
        var username = AuthController.getUsernameFromJWT(message.jwt);
        if(isOthersPlayersTurn(username)) return new KOMsg(ReturnMessage.NOT_YOUR_TURN.text);

        try {
            boolean lastRound = this.gameStateController.endActionTurn();
            //todo: fix
            try {
                this.gameStateController.gameState.createSnapshot();
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    protected Map<String, Boolean> getUsernameToWinnerMap(Map<Integer, Boolean> schoolBoardIdToWinnerMap){
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
        var userSchoolBoardId = usernameToSchoolBoardId.get(username);
        var userTowerColor = this.gameStateController.getSchoolBoardTowerColorFromID(userSchoolBoardId);
        var teamSchoolBoardIDs = this.gameStateController.getSchoolBoardIDFromTowerColor(userTowerColor);

        List<String> teamUsernames = new LinkedList<>();

        this.usernameToSchoolBoardId.forEach((user, sbID) -> {
            if(teamSchoolBoardIDs.contains(sbID)) teamUsernames.add(user);
        });

        return teamUsernames;
    }

    /**
     * Return light game state from the communication controller
     * @return light game state
     */
    public LightGameState getLightGameState() {
        return this.gameStateController.getLightGameState().addUsernames(usernameToSchoolBoardId);
    }

    public UUID getGameUUID() {
        return this.gameStateController.getGameUUID();
    }

    private int getSchoolBoardIdFromUsername(String player){
        return this.usernameToSchoolBoardId.get(player);
    }

    private String getUsernameFromSchoolBoardId(int schoolBoardId){
        var returnString = this.usernameToSchoolBoardId.entrySet().stream().filter(entry -> entry.getValue() == schoolBoardId).map(Map.Entry::getKey).findFirst();

        if(returnString.isEmpty()) throw new InvalidSchoolBoardIdException("Fatal error - invalid schoolBoard ID.");

        return returnString.get();
    }

    @SugarMessageHandler
    public void base(SugarMessage message, Peer peer) throws UnhandledMessageAtLowestLayerException {
        System.out.println("Dropping message : " + message.serialize());
        throw new UnhandledMessageAtLowestLayerException(message);
    }
}
