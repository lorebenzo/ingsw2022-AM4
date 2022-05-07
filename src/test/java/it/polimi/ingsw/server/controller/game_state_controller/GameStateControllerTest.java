package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.server.controller.game_state_controller.GameStateController;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GameStateControllerTest {

/*    @Test
    public void playCard1() throws GameStateInitializationFailureException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException {
        GameStateController gameStateController = new GameStateController();

        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        assertFalse(gameStateController.cardPlayed());
        gameStateController.playCard(Card.DOG);
        assertTrue(gameStateController.cardPlayed());

        assertThrows(MoveAlreadyPlayedException.class, ()-> gameStateController.playCard(Card.GOOSE));
    }*/

    //WrongPhaseExcepion
    @Test
    public void moveStudentFromEntranceToDiningRoom1() throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();
        gameStateController.setCurrentPhaseForTesting(Phase.PLANNING);

        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        Color student = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);

        assertThrows(WrongPhaseException.class, () -> gameStateController.moveStudentFromEntranceToDiningRoom(student));
    }

    //TooManyStudentsMovedExcepion
    @Test
    public void moveStudentFromEntranceToDiningRoom2() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();
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
    public void moveStudentFromEntranceToDiningRoom3() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();
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
    public void moveStudentFromEntranceToArchipelago1() throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();
        gameStateController.setCurrentPhaseForTesting(Phase.PLANNING);

        List<Integer> islandCodes = gameStateController.getGameStateForTesting().getArchipelagosForTesting().get(0).getIslandCodes();

        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        Color student = gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0);

        assertThrows(WrongPhaseException.class, () -> gameStateController.moveStudentFromEntranceToArchipelago(student, islandCodes));

    }

    //TooManyStudentsMovedExcepion
    @Test
    public void moveStudentFromEntranceToArchipelago2() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();
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
    public void moveStudentFromEntranceToArchipelago3() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();
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
    public void moveMotherNature1() throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

        gameStateController.setCurrentPhaseForTesting(Phase.PLANNING);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);


        assertThrows(WrongPhaseException.class, () -> gameStateController.moveMotherNature(1));
    }

    //MoreStudentsToBeMovedException
    @Test
    public void moveMotherNature2() throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        assertThrows(MoreStudentsToBeMovedException.class, () -> gameStateController.moveMotherNature(1));
    }

    //MoveAlreadyPlayedExcepion
    @Test
    public void moveMotherNature3() throws GameStateInitializationFailureException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

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
    public void grabStudentsFromCloud1() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

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
    public void grabStudentsFromCloud2() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));


        assertThrows(MotherNatureToBeMovedException.class, () -> gameStateController.grabStudentsFromCloud(0));

    }

    @Test
    public void grabStudentsFromCloud3() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException, MotherNatureToBeMovedException, EmptyCloudException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

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
    public void endActionTurn1() throws GameStateInitializationFailureException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException, MotherNatureToBeMovedException, EmptyCloudException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

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
    public void endActionTurn2() throws GameStateInitializationFailureException, EmptyStudentSupplyException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MoveAlreadyPlayedException {
        GameStateController gameStateController = new GameStateController();

        //gameStateController.getGameStateForTesting().fillClouds();

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
    public void endActionTurn3() throws GameStateInitializationFailureException, EmptyStudentSupplyException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException {
        GameStateController gameStateController = new GameStateController();

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
    public void endActionTurn4() throws GameStateInitializationFailureException, EmptyStudentSupplyException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException {
        GameStateController gameStateController = new GameStateController();

        //gameStateController.getGameStateForTesting().fillClouds();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));


        assertThrows(MoreStudentsToBeMovedException.class, gameStateController::endActionTurn);

    }

    @Test
    public void endActionTurn5() throws GameStateInitializationFailureException, EmptyStudentSupplyException, TooManyStudentsMovedException, StudentNotInTheEntranceException, WrongPhaseException, FullDiningRoomLaneException {
        GameStateController gameStateController = new GameStateController();

        //gameStateController.getGameStateForTesting().fillClouds();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));

        assertThrows(MoreStudentsToBeMovedException.class, gameStateController::endActionTurn);

    }

    @Test
    public void endActionTurn6() throws GameStateInitializationFailureException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

        //gameStateController.getGameStateForTesting().fillClouds();

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
        gameStateController.getGameStateForTesting().setCurrentPlayerSchoolBoardId(0);

        gameStateController.getGameStateForTesting().getSchoolBoardIdsToCardsPlayedThisRoundForTesting().put(0,Card.DOG);

        assertThrows(MoreStudentsToBeMovedException.class, gameStateController::endActionTurn);

    }

    @Test
    public void gameFlow1() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

        gameStateController.playCard(Card.DOG);
        assertThrows(InvalidCardPlayedException.class, () -> gameStateController.playCard(Card.DOG));
    }

    @Test
    public void gameFlow2() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

        gameStateController.playCard(Card.DOG);
        gameStateController.playCard(Card.GOOSE);

        assertEquals(0, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

    }

    @Test
    public void gameFlow3() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

        gameStateController.playCard(Card.GOOSE);
        gameStateController.playCard(Card.DOG);

        assertEquals(1, gameStateController.getGameStateForTesting().getCurrentPlayerSchoolBoardId());

    }


    @Test
    public void oneCompleteRound() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MotherNatureToBeMovedException, EmptyCloudException, StudentsToBeGrabbedFromCloudException, GameOverException, CardNotPlayedException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

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
    public void twoCompleteRoundsFail() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MotherNatureToBeMovedException, EmptyCloudException, StudentsToBeGrabbedFromCloudException, GameOverException, CardNotPlayedException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

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
    public void twoCompleteRoundsSuccess() throws GameStateInitializationFailureException, WrongPhaseException, MoveAlreadyPlayedException, InvalidCardPlayedException, CardIsNotInTheDeckException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, InvalidNumberOfStepsException, MotherNatureToBeMovedException, EmptyCloudException, StudentsToBeGrabbedFromCloudException, GameOverException, CardNotPlayedException, EmptyStudentSupplyException {
        GameStateController gameStateController = new GameStateController();

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

}
