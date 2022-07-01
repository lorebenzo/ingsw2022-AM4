package it.polimi.ingsw.server.controller.game_state_controller;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.*;
import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.PlayableCharacter;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.Phase;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class ExpertGameStateControllerTest {

    @Test
    private void addAllCharacters(GameStateController gameStateController) throws NoSuchFieldException, IllegalAccessException {
        Field availableCharacters = gameStateController.gameState.getClass().getDeclaredField("availableCharacters");
        availableCharacters.setAccessible(true);
        List<PlayableCharacter> allCharacters = new ArrayList<>();

        for (Character character: Character.values()) {
            PlayableCharacter tmp = PlayableCharacter.createCharacter(character);
            for (int i = 0; i < tmp.getInitialStudentsNumberOnCharacter(); i++) {
                tmp.addStudent(Color.PURPLE);
            }
            allCharacters.add(tmp);
        }
        availableCharacters.set(gameStateController.gameState, allCharacters);
    }

    //CharacterIndex
    @Test
    public void applyEffectCharacterIndexWrongPhase() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        assertThrows(WrongPhaseException.class, () -> gameStateController.applyEffect(2));
    }

    @Test
    public void applyEffectCharacterIndexMoveAlreadyPlayed() throws GameStateInitializationFailureException, InvalidCharacterIndexException, NotEnoughCoinsException, MoveAlreadyPlayedException, WrongArgumentsException, WrongPhaseException, MoveNotAvailableException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(-10);

        gameStateController.applyEffect(2);

        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(2));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(9,Color.RED));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(3,0));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(1,Color.RED, 0));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(7,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));

    }

    @Test
    public void applyEffectCharacterIndexInvalidCharacterIndex() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(gameStateController.gameState.getAvailableCharacters().size()));
        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(-1));
    }

    @Test
    public void applyEffectCharacterIndexWrongArguments() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        ExpertGameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);

        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(2, Color.RED));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(2,0));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(2,Color.RED,0));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(2,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));
    }

    @Test
    public void applyEffectCharacterIndexNotEnoughCoins() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(NotEnoughCoinsException.class, () -> gameStateController.applyEffect(2));

    }

    //CharacterIndexColor
    @Test
    public void applyEffectCharacterIndexColorWrongPhase() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        assertThrows(WrongPhaseException.class, () -> gameStateController.applyEffect(9,Color.RED));
    }

    @Test
    public void applyEffectCharacterIndexColorMoveAlreadyPlayed() throws GameStateInitializationFailureException, FullDiningRoomLaneException, InvalidCharacterIndexException, NotEnoughCoinsException, StudentsNotInTheDiningRoomException, MoveAlreadyPlayedException, StudentNotOnCharacterException, WrongArgumentsException, WrongPhaseException, MoveNotAvailableException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(-10);

        gameStateController.applyEffect(9,Color.RED);

        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(2));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(9,Color.RED));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(3,0));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(1,Color.RED, 0));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(7,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));

    }

    @Test
    public void applyEffectCharacterIndexColorInvalidCharacterIndex() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(gameStateController.gameState.getAvailableCharacters().size(), Color.RED));
        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(-1, Color.RED));
    }

    @Test
    public void applyEffectCharacterIndexColorWrongArguments() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(9));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(9,0));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(9,Color.RED,0));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(9,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));
    }

    @Test
    public void applyEffectCharacterIndexColorNotEnoughCoins() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(NotEnoughCoinsException.class, () -> gameStateController.applyEffect(9,Color.RED));
    }

    //CharacterIndexArchipelago
    @Test
    public void applyEffectCharacterIndexArchipelagoWrongPhase() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        assertThrows(WrongPhaseException.class, () -> gameStateController.applyEffect(3,0));
    }

    @Test
    public void applyEffectCharacterIndexArchipelagoMoveAlreadyPlayed() throws GameStateInitializationFailureException, InvalidCharacterIndexException, NotEnoughCoinsException, MoveAlreadyPlayedException, WrongArgumentsException, WrongPhaseException, MoveNotAvailableException, InvalidArchipelagoIdException, NoAvailableLockException, ArchipelagoAlreadyLockedException, GameOverException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(-10);

        gameStateController.applyEffect(3,0);

        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(2));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(9,Color.RED));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(3,0));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(1,Color.RED, 0));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(7,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));
    }

    @Test
    public void applyEffectCharacterIndexArchipelagoInvalidCharacterIndex() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(gameStateController.gameState.getAvailableCharacters().size(), 0));
        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(-1, 0));
    }

    @Test
    public void applyEffectCharacterIndexArchipelagoWrongArguments() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(3));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(3, Color.RED));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(3,Color.RED,0));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(3,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));
    }

    @Test
    public void applyEffectCharacterIndexArchipelagoNotEnoughCoins() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(NotEnoughCoinsException.class, () -> gameStateController.applyEffect(3,0));
    }

    //CharacterIndexColorArchipelago
    @Test
    public void applyEffectCharacterIndexColorArchipelagoWrongPhase() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        assertThrows(WrongPhaseException.class, () -> gameStateController.applyEffect(1,Color.RED,0));
    }

    @Test
    public void applyEffectCharacterIndexColorArchipelagoMoveAlreadyPlayed() throws GameStateInitializationFailureException, InvalidCharacterIndexException, NotEnoughCoinsException, MoveAlreadyPlayedException, StudentNotOnCharacterException, WrongArgumentsException, WrongPhaseException, MoveNotAvailableException, InvalidArchipelagoIdException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(-10);

        Optional<PlayableCharacter> tmp = gameStateController.gameState.getAvailableCharacters().stream().filter(playableCharacter -> playableCharacter.getCharacterId() == 1).findFirst();

        Color studentToGet = null;
        if(tmp.isPresent()){
            studentToGet = tmp.get().getStudents().get(0);
        }
        else
            fail();


        gameStateController.applyEffect(1,studentToGet, 0);

        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(2));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(9,Color.RED));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(3,0));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(1,Color.RED, 0));
        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(7,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));

    }

    @Test
    public void applyEffectCharacterIndexColorArchipelagoInvalidCharacterIndex() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(gameStateController.gameState.getAvailableCharacters().size(), Color.RED,0));
        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(-1, Color.RED,0));
    }

    @Test
    public void applyEffectCharacterIndexColorArchipelagoWrongArguments() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(1));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(1,Color.RED));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(1,0));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(1,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));
    }

    @Test
    public void applyEffectCharacterIndexColorArchipelagoNotEnoughCoins() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(1);


        Optional<PlayableCharacter> tmp = gameStateController.gameState.getAvailableCharacters().stream().filter(playableCharacter -> playableCharacter.getCharacterId() == 1).findFirst();

        Color studentToGet = null;
        if(tmp.isPresent()){
            studentToGet = tmp.get().getStudents().get(0);
        }
        else
            fail();

        Color finalStudentToGet = studentToGet;
        assertThrows(NotEnoughCoinsException.class, () -> gameStateController.applyEffect(1, finalStudentToGet,0));
    }

    //CharacterIndexColorList
    @Test
    public void applyEffectCharacterIndexColorListWrongPhase() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        assertThrows(WrongPhaseException.class, () -> gameStateController.applyEffect(7,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));
    }

//    @Test
//    public void applyEffectCharacterIndexColorListMoveAlreadyPlayed() throws GameStateInitializationFailureException, InvalidCharacterIndexException, NotEnoughCoinsException, MoveAlreadyPlayedException, WrongArgumentsException, WrongPhaseException, MoveNotAvailableException, StudentsNotInTheDiningRoomException, StudentNotInTheEntranceException, StudentNotOnCharacterException, InvalidStudentListsLengthException, FullDiningRoomLaneException, NoSuchFieldException, IllegalAccessException {
//        GameStateController gameStateController = new ExpertGameStateController(2);
//        this.addAllCharacters(gameStateController);
//        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);
//
//        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(-10);
//
//        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().addStudentToEntrance(Color.RED);
//        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().moveFromEntranceToDiningRoom(Color.RED);
//
//        gameStateController.applyEffect(10,
//                new LinkedList<>(List.of(Color.RED)),
//                new LinkedList<>(List.of(gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0))));
//
//        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(2));
//        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(9,Color.RED));
//        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(3,0));
//        assertThrows(MoveAlreadyPlayedException.class, () -> gameStateController.applyEffect(1,Color.RED, 0));
//
//    }

    @Test
    public void applyEffectCharacterIndexColorListInvalidCharacterIndex() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(gameStateController.gameState.getAvailableCharacters().size(),new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));
        assertThrows(InvalidCharacterIndexException.class, () -> gameStateController.applyEffect(-1,new LinkedList<>(List.of(Color.RED)),new LinkedList<>(List.of(Color.RED))));
    }

    @Test
    public void applyEffectCharacterIndexColorListWrongArguments() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(7));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(7, Color.RED));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(7,0));
        assertThrows(WrongArgumentsException.class, () -> gameStateController.applyEffect(7,Color.RED,0));
    }

    @Test
    public void applyEffectCharacterIndexColorListNotEnoughCoins() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        gameStateController.setCurrentPhaseForTesting(Phase.ACTION);

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(1);

        assertThrows(NotEnoughCoinsException.class, () -> gameStateController.applyEffect(2));

    }

    @Test
    public void playMoveMotherNatureToAnyArchipelagoGameOverTowers() throws GameStateInitializationFailureException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, NotEnoughCoinsException, InvalidCharacterIndexException, InvalidArchipelagoIdException, WrongArgumentsException, NoAvailableLockException, ArchipelagoAlreadyLockedException, MoveNotAvailableException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
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

        for (int i = 0; i < 3; i++) {
            gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        }

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(-2);

        try{
            gameStateController.applyEffect(3,7);
        }catch (GameOverException e){
            assertTrue(e.schoolBoardIdToWinnerMap.get(0));
            assertFalse(e.schoolBoardIdToWinnerMap.get(1));
        }
    }

    @Test
    public void playMoveMotherNatureToAnyArchipelagoGameOverThreeArchipelagos() throws GameStateInitializationFailureException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, NotEnoughCoinsException, InvalidCharacterIndexException, InvalidArchipelagoIdException, WrongArgumentsException, NoAvailableLockException, ArchipelagoAlreadyLockedException, MoveNotAvailableException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(2);
        this.addAllCharacters(gameStateController);
        for (int i = 0; i < 6; i++) {
            gameStateController.gameState.setMotherNaturePositionForTesting(gameStateController.gameState.getArchipelagosForTesting().get(i));
            gameStateController.gameState.conquerArchipelago(gameStateController.getCurrentPlayerSchoolBoardId());
        }

        gameStateController.gameState.setCurrentPlayerSchoolBoardId(1);

        for (int i = 0; i < 4; i++) {
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


        for (int i = 0; i < 3; i++) {
            gameStateController.moveStudentFromEntranceToDiningRoom(gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance().get(0));
        }

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(-2);

        try{
            gameStateController.applyEffect(3,0);
        }catch (GameOverException e){
            assertTrue(e.schoolBoardIdToWinnerMap.get(0));
            assertFalse(e.schoolBoardIdToWinnerMap.get(1));
        }
    }

    @Test
    public void playMoveMotherNatureToAnyArchipelagoGameOverSameTowers() throws GameStateInitializationFailureException, InvalidNumberOfStepsException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(3);
        this.addAllCharacters(gameStateController);
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

        gameStateController.gameState.getCurrentPlayerSchoolBoardForTesting().payCharacter(-2);

        try{
            gameStateController.moveMotherNature(1);
        }catch (GameOverException e){
            assertTrue(e.schoolBoardIdToWinnerMap.get(0));
            assertFalse(e.schoolBoardIdToWinnerMap.get(1));
            assertFalse(e.schoolBoardIdToWinnerMap.get(2));
        }
    }

    @Test
    public void playMoveMotherNatureToAnyArchipelagoGameOverSameTowersAndProfessors() throws GameStateInitializationFailureException, InvalidNumberOfStepsException, InvalidCardPlayedException, CardIsNotInTheDeckException, MoveAlreadyPlayedException, WrongPhaseException, TooManyStudentsMovedException, StudentNotInTheEntranceException, FullDiningRoomLaneException, MoreStudentsToBeMovedException, NoSuchFieldException, IllegalAccessException {
        GameStateController gameStateController = new ExpertGameStateController(3);
        this.addAllCharacters(gameStateController);
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
    void nextActionTurn() throws GameStateInitializationFailureException, NoSuchFieldException, IllegalAccessException, InvalidCharacterIndexException, NotEnoughCoinsException, InvalidArchipelagoIdException, MoveAlreadyPlayedException, StudentNotOnCharacterException, WrongArgumentsException, WrongPhaseException, MoveNotAvailableException, GameOverException {
        GameStateController gameStateController = new ExpertGameStateController(2);

        this.addAllCharacters(gameStateController);

        Color student = gameStateController.gameState.getAvailableCharacters().get(1).getStudents().get(0);

        gameStateController.gameState.setCurrentPhase(Phase.ACTION);

        gameStateController.applyEffect(1,student,0);

        assertEquals(3,gameStateController.gameState.getAvailableCharacters().get(1).getStudents().size());
        assertTrue(gameStateController.gameState.wasCharacterPlayedInCurrentTurn());

        gameStateController.nextActionTurn();
        assertEquals(4,gameStateController.gameState.getAvailableCharacters().get(1).getStudents().size());

        assertTrue(gameStateController.gameState.getArchipelagosForTesting().get(0).getStudents().contains(student));
        assertFalse(gameStateController.gameState.wasCharacterPlayedInCurrentTurn());
    }
}
