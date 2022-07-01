package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GameStateControllerTest {

    //WrongPhaseExcepion
    @Test
    public void moveStudentFromEntranceToDiningRoom1() throws GameStateInitializationFailureException {
        GameStateController gameStateController = new GameStateController(2);
        gameStateController.setCurrentPhaseForTesting(Phase.PLANNING);

        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        Color student = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);

        assertThrows(WrongPhaseException.class, () -> gameStateController.moveStudentFromEntranceToDiningRoom(student));
    }

    //TooManyStudentsMovedExcepion
    @Test
    public void moveStudentFromEntranceToDiningRoom2() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException {
        GameStateController gameStateController = new GameStateController(2);
        Color student;

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        for (int i = 0; i < gameStateController.getGameStateForTesting().getNumberOfMovableStudents();i++){
            student = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
            gameStateController.moveStudentFromEntranceToDiningRoom(student);
        }

        Color student2 = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        assertThrows(TooManyStudentsMovedException.class, () -> gameStateController.moveStudentFromEntranceToDiningRoom(student2));
    }

    //TooManyStudentsMovedExcepion - MotherNatureToBeMoved
    @Test
    public void moveStudentFromEntranceToDiningRoom3() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException {
        GameStateController gameStateController = new GameStateController(2);
        Color student;

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        for (int i = 0; i < gameStateController.getGameStateForTesting().getNumberOfMovableStudents();i++){
            student = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
            gameStateController.moveStudentFromEntranceToDiningRoom(student);
        }

        assertThrows(MotherNatureToBeMovedException.class, () -> gameStateController.grabStudentsFromCloud(0));


        Color student2 = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        assertThrows(TooManyStudentsMovedException.class, () -> gameStateController.moveStudentFromEntranceToDiningRoom(student2));
    }


    //WrongPhaseExcepion
    @Test
    public void moveStudentFromEntranceToArchipelago1() throws GameStateInitializationFailureException {
        GameStateController gameStateController = new GameStateController(2);
        gameStateController.setCurrentPhaseForTesting(Phase.PLANNING);

        List<Integer> islandCodes = gameStateController.getGameStateForTesting().getArchipelagosForTesting().get(0).getIslandCodes();

        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        Color student = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);

        assertThrows(WrongPhaseException.class, () -> gameStateController.moveStudentFromEntranceToArchipelago(student, islandCodes));

    }

    //TooManyStudentsMovedExcepion
    @Test
    public void moveStudentFromEntranceToArchipelago2() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException {
        GameStateController gameStateController = new GameStateController(2);
        Color student;

        List<Integer> islandCodes = gameStateController.getGameStateForTesting().getArchipelagosForTesting().get(0).getIslandCodes();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        for (int i = 0; i < gameStateController.getGameStateForTesting().getNumberOfMovableStudents();i++){
            student = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
            gameStateController.moveStudentFromEntranceToArchipelago(student,islandCodes);
        }

        Color student2 = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        assertThrows(TooManyStudentsMovedException.class, () -> gameStateController.moveStudentFromEntranceToArchipelago(student2,islandCodes));
    }

    //TooManyStudentsMovedExcepion - MotherNatureToBeMoved
    @Test
    public void moveStudentFromEntranceToArchipelago3() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException {
        GameStateController gameStateController = new GameStateController(2);
        Color student;

        List<Integer> islandCodes = gameStateController.getGameStateForTesting().getArchipelagosForTesting().get(0).getIslandCodes();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        for (int i = 0; i < gameStateController.getGameStateForTesting().getNumberOfMovableStudents();i++){
            student = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
            gameStateController.moveStudentFromEntranceToArchipelago(student,islandCodes);
        }

        assertThrows(MotherNatureToBeMovedException.class, () -> gameStateController.grabStudentsFromCloud(0));


        Color student2 = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);
        assertThrows(TooManyStudentsMovedException.class, () -> gameStateController.moveStudentFromEntranceToArchipelago(student2,islandCodes));
    }

    //WrongPhaseExeption
    @Test
    public void moveMotherNature1() throws GameStateInitializationFailureException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.setCurrentPhaseForTesting(Phase.PLANNING);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);


        assertThrows(WrongPhaseException.class, () -> gameStateController.moveMotherNature(1));
    }

    //MoreStudentsToBeMovedException
    @Test
    public void moveMotherNature2() throws GameStateInitializationFailureException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        assertThrows(MoreStudentsToBeMovedException.class, () -> gameStateController.moveMotherNature(1));
    }

    //MoveAlreadyPlayedExcepion
    @Test
    public void moveMotherNature3() throws GameStateInitializationFailureException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, GameOverException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.moveMotherNature(1));
    }

    @Test
    public void grabStudentsFromCloud1() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException, GameOverException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);

        gameStateController.setCurrentPhaseForTesting(Phase.PLANNING);
        assertThrows(WrongPhaseException.class, () -> gameStateController.grabStudentsFromCloud(0));

    }

    @Test
    public void grabStudentsFromCloud2() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));


        assertThrows(MotherNatureToBeMovedException.class, () -> gameStateController.grabStudentsFromCloud(0));

    }

    @Test
    public void grabStudentsFromCloud3() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException, MotherNatureToBeMovedException, EmptyCloudException, GameOverException {
        GameStateController gameStateController = new GameStateController(2);

        //gameStateController.getGameStateForTesting().fillClouds();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);

        gameStateController.grabStudentsFromCloud(0);
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.grabStudentsFromCloud(1));

    }

    @Test
    public void endActionTurn1() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException, MotherNatureToBeMovedException, EmptyCloudException, GameOverException {
        GameStateController gameStateController = new GameStateController(2);

        //gameStateController.getGameStateForTesting().fillClouds();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(0);

        gameStateController.setCurrentPhaseForTesting(Phase.PLANNING);
        assertThrows(WrongPhaseException.class, gameStateController::endActionTurn);
    }

    @Test
    public void endActionTurn2() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException, GameOverException {
        GameStateController gameStateController = new GameStateController(2);


        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);

        assertThrows(StudentsToBeGrabbedFromCloudException.class, gameStateController::endActionTurn);

    }

    @Test
    public void endActionTurn3() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException {
        GameStateController gameStateController = new GameStateController(2);

        //gameStateController.getGameStateForTesting().fillClouds();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));


        assertThrows(MotherNatureToBeMovedException.class, gameStateController::endActionTurn);

    }

    @Test
    public void endActionTurn4() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException {
        GameStateController gameStateController = new GameStateController(2);

        //gameStateController.getGameStateForTesting().fillClouds();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));


        assertThrows(MoreStudentsToBeMovedException.class, gameStateController::endActionTurn);

    }

    @Test
    public void endActionTurn5() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException {
        GameStateController gameStateController = new GameStateController(2);

        //gameStateController.getGameStateForTesting().fillClouds();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        assertThrows(MoreStudentsToBeMovedException.class, gameStateController::endActionTurn);

    }

    @Test
    public void endActionTurn6() throws GameStateInitializationFailureException {
        GameStateController gameStateController = new GameStateController(2);

        //gameStateController.getGameStateForTesting().fillClouds();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        assertThrows(MoreStudentsToBeMovedException.class, gameStateController::endActionTurn);

    }

    @Test
    public void gameFlow1() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.playCard(Card.DOG);
        assertThrows(InvalidCardPlayedException.class, () -> gameStateController.playCard(Card.DOG));
    }

    @Test
    public void gameFlow2() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.playCard(Card.DOG);
        gameStateController.playCard(Card.GOOSE);

        assertEquals(0, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

    }

    @Test
    public void gameFlow3() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.playCard(Card.GOOSE);
        gameStateController.playCard(Card.DOG);

        assertEquals(1, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

    }

    @Test
    public void oneCompleteRound() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MotherNatureToBeMovedException, EmptyCloudException, StudentsToBeGrabbedFromCloudException, GameOverException, CardNotPlayedException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.playCard(Card.DOG);
        gameStateController.playCard(Card.GOOSE);

        assertEquals(0, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(0);
        gameStateController.endActionTurn();

        assertEquals(1, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(1);
        gameStateController.endActionTurn();


    }

    //Should throw CardIsNotInTheDeckException
    @Test
    public void twoCompleteRoundsFail() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MotherNatureToBeMovedException, EmptyCloudException, StudentsToBeGrabbedFromCloudException, GameOverException, CardNotPlayedException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.playCard(Card.DOG);
        gameStateController.playCard(Card.GOOSE);

        assertEquals(0, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(0);
        gameStateController.endActionTurn();

        assertEquals(1, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(1);
        gameStateController.endActionTurn();

        assertThrows(CardIsNotInTheDeckException.class, () -> gameStateController.playCard(Card.DOG));

    }

    @Test
    public void twoCompleteRoundsSuccess() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MotherNatureToBeMovedException, EmptyCloudException, StudentsToBeGrabbedFromCloudException, GameOverException, CardNotPlayedException {
        GameStateController gameStateController = new GameStateController(2);

        gameStateController.playCard(Card.DOG);
        gameStateController.playCard(Card.GOOSE);

        assertEquals(0, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(0);
        gameStateController.endActionTurn();

        assertEquals(1, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(1);
        gameStateController.endActionTurn();


        gameStateController.playCard(Card.GOOSE);
        gameStateController.playCard(Card.DOG);

        assertEquals(1, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(0);
        gameStateController.endActionTurn();

        assertEquals(0, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(1);
        gameStateController.endActionTurn();
    }

    @Test
    public void gameOverTowers() throws GameStateInitializationFailureException, InvalidNumberOfStepsException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException {
        GameStateController gameStateController = new GameStateController(2);

        for (int i = 0; i < 7; i++) {
            gameStateController.gameState.moveMotherNatureOneStepClockwise();
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
            gameStateController.gameState.mergeWithPrevious();
        }


        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.RED);

        for(Archipelago archipelago: gameStateController.gameState.getArchipelagosForTesting()) {
            archipelago.addStudent(Color.RED);
        }

        gameStateController.playCard(Card.DOG);
        gameStateController.playCard(Card.GOOSE);

        for (int i = 0; i < 3; i++) {
            gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        }

        assertFalse(gameStateController.gameState.checkWinners().containsValue(true));

        try{
            gameStateController.moveMotherNature(1);
        }catch (GameOverException e){
            assertTrue(e.schoolBoardIdToWinnerMap.get(0));
            assertFalse(e.schoolBoardIdToWinnerMap.get(1));
        }
    }

    @Test
    public void gameOverThreeArchipelagos() throws GameStateInitializationFailureException, InvalidNumberOfStepsException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException {
        GameStateController gameStateController = new GameStateController(2);

        for (int i = 0; i < 6; i++) {
            gameStateController.gameState.moveMotherNatureOneStepClockwise();
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
            gameStateController.gameState.mergeWithPrevious();
        }

        gameStateController.gameState.setCurrentPlayerSchoolBoardId(1);

        for (int i = 0; i < 4; i++) {
            gameStateController.gameState.moveMotherNatureOneStepClockwise();
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
            gameStateController.gameState.mergeWithPrevious();
        }

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.RED);

        for(Archipelago archipelago: gameStateController.gameState.getArchipelagosForTesting()) {
            archipelago.addStudent(Color.RED);
        }

        gameStateController.gameState.setCurrentPlayerSchoolBoardId(0);
        gameStateController.playCard(Card.GOOSE);
        gameStateController.playCard(Card.DOG);


        for (int i = 0; i < 3; i++) {
            gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        }

        assertFalse(gameStateController.gameState.checkWinners().containsValue(true));

        try{
            gameStateController.moveMotherNature(1);
        }catch (GameOverException e){
            assertTrue(e.schoolBoardIdToWinnerMap.get(0));
            assertFalse(e.schoolBoardIdToWinnerMap.get(1));
        }
    }

    @Test
    public void gameOverSameTowers() throws GameStateInitializationFailureException, InvalidNumberOfStepsException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException {
        GameStateController gameStateController = new GameStateController(3);

        for (int i = 0; i < 5; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }
        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.GREEN);
        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.CYAN);
        gameStateController.gameState.setCurrentPlayerSchoolBoardId(1);

        for (int i = 0; i < 5; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }
        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.YELLOW);
        gameStateController.gameState.setCurrentPlayerSchoolBoardId(2);

        for (int i = 0; i < 1; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.RED);

        for(Archipelago archipelago: gameStateController.gameState.getArchipelagosForTesting()) {
            archipelago.addStudent(Color.RED);
        }

        gameStateController.gameState.setCurrentPlayerSchoolBoardId(0);
        gameStateController.playCard(Card.FOX);
        gameStateController.playCard(Card.GOOSE);
        gameStateController.playCard(Card.DOG);


        for (int i = 0; i < 4; i++) {
            gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        }

        try{
            gameStateController.moveMotherNature(1);
        }catch (GameOverException e){
            assertTrue(e.schoolBoardIdToWinnerMap.get(0));
            assertFalse(e.schoolBoardIdToWinnerMap.get(1));
            assertFalse(e.schoolBoardIdToWinnerMap.get(2));
        }
    }

    @Test
    public void gameOverSameTowersAndProfessors() throws GameStateInitializationFailureException, InvalidNumberOfStepsException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException {
        GameStateController gameStateController = new GameStateController(3);

        for (int i = 0; i < 5; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }
        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.GREEN);
        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.CYAN);
        gameStateController.gameState.setCurrentPlayerSchoolBoardId(1);

        for (int i = 0; i < 5; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }
        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.YELLOW);
        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.PURPLE);
        gameStateController.gameState.setCurrentPlayerSchoolBoardId(2);

        for (int i = 0; i < 1; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.RED);

        for(Archipelago archipelago: gameStateController.gameState.getArchipelagosForTesting()) {
            archipelago.addStudent(Color.RED);
        }

        gameStateController.gameState.setCurrentPlayerSchoolBoardId(0);
        gameStateController.playCard(Card.FOX);
        gameStateController.playCard(Card.GOOSE);
        gameStateController.playCard(Card.DOG);


        for (int i = 0; i < 4; i++) {
            gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        }

        try{
            gameStateController.moveMotherNature(1);
        }catch (GameOverException e){
            assertTrue(e.schoolBoardIdToWinnerMap.get(0));
            assertTrue(e.schoolBoardIdToWinnerMap.get(1));
            assertFalse(e.schoolBoardIdToWinnerMap.get(2));
        }
    }

    @Test
    public void gameOverCards() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, TooManyStudentsMovedException, StudentNotInTheEntranceException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MotherNatureToBeMovedException, EmptyCloudException, StudentsToBeGrabbedFromCloudException, GameOverException, CardNotPlayedException {
        GameStateController gameStateController = new GameStateController(2);

        for (int j = 0; j < Card.values().length-1; j++) {
            gameStateController.playCard(List.of(Card.values()).get(j));
            gameStateController.playCard(List.of(Card.values()).get(j+1));

            assertEquals(0, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

            for (int i = 0; i < 3; i++) {
                gameStateController.moveStudentFromEntranceToArchipelago(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0),gameStateController.gameState.getArchipelagosForTesting().get(0).getIslandCodes());
            }

            gameStateController.moveMotherNature(1);
            gameStateController.grabStudentsFromCloud(0);
            gameStateController.endActionTurn();

            assertEquals(1, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

            for (int i = 0; i < 3; i++) {
                gameStateController.moveStudentFromEntranceToArchipelago(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0),gameStateController.gameState.getArchipelagosForTesting().get(0).getIslandCodes());
            }

            gameStateController.moveMotherNature(1);
            gameStateController.grabStudentsFromCloud(1);
            gameStateController.endActionTurn();
        }

        assertTrue(gameStateController.playCard(List.of(Card.values()).get(9)));
        assertTrue(gameStateController.playCard(List.of(Card.values()).get(0)));


        assertEquals(1, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        for (int i = 0; i < 3; i++) {
            gameStateController.moveStudentFromEntranceToArchipelago(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0),gameStateController.gameState.getArchipelagosForTesting().get(0).getIslandCodes());
        }

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(1);
        gameStateController.endActionTurn();

        assertEquals(0, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

        for (int i = 0; i < 3; i++) {
            gameStateController.moveStudentFromEntranceToArchipelago(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0),gameStateController.gameState.getArchipelagosForTesting().get(0).getIslandCodes());
        }

        gameStateController.moveMotherNature(1);
        gameStateController.grabStudentsFromCloud(0);
        assertThrows(GameOverException.class, gameStateController::endActionTurn);
    }

    @Test
    public void gameOverTowers4() throws GameStateInitializationFailureException, InvalidNumberOfStepsException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException {
        GameStateController gameStateController = new GameStateController(4);

        for (int i = 0; i < 7; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.RED);

        for(Archipelago archipelago: gameStateController.gameState.getArchipelagosForTesting()) {
            archipelago.addStudent(Color.RED);
        }

        gameStateController.playCard(Card.DOG);
        gameStateController.playCard(Card.GOOSE);
        gameStateController.playCard(Card.CAT);
        gameStateController.playCard(Card.PARROT);

        for (int i = 0; i < 3; i++) {
            gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        }

        try{
            gameStateController.moveMotherNature(1);
        }catch (GameOverException e){
            assertTrue(e.schoolBoardIdToWinnerMap.get(0));
            assertFalse(e.schoolBoardIdToWinnerMap.get(1));
            assertTrue(e.schoolBoardIdToWinnerMap.get(2));
            assertFalse(e.schoolBoardIdToWinnerMap.get(3));
        }
    }

    @Test
    public void gameOverThreeArchipelagos4() throws GameStateInitializationFailureException, InvalidNumberOfStepsException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException {
        GameStateController gameStateController = new GameStateController(4);

        for (int i = 0; i < 7; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }

        gameStateController.gameState.setCurrentPlayerSchoolBoardId(1);

        for (int i = 0; i < 3; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addProfessor(Color.RED);

        for(Archipelago archipelago: gameStateController.gameState.getArchipelagosForTesting()) {
            archipelago.addStudent(Color.RED);
        }

        gameStateController.gameState.setCurrentPlayerSchoolBoardId(0);
        gameStateController.playCard(Card.GOOSE);
        gameStateController.playCard(Card.DOG);
        gameStateController.playCard(Card.CAT);
        gameStateController.playCard(Card.PARROT);


        for (int i = 0; i < 3; i++) {
            gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        }

        try{
            gameStateController.moveMotherNature(1);
        }catch (GameOverException e){
            assertTrue(e.schoolBoardIdToWinnerMap.get(0));
            assertFalse(e.schoolBoardIdToWinnerMap.get(1));
            assertTrue(e.schoolBoardIdToWinnerMap.get(2));
            assertFalse(e.schoolBoardIdToWinnerMap.get(3));
        }
    }

}
