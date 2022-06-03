package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.controller.game_state_controller.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidCardPlayedException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.StudentNotInTheEntranceException;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.exceptions.*;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
public class PlayableCharacterTest {

    @Test
    public void containsAllStudents() {
        PlayableCharacter playableCharacter = new PlayableCharacterWithStudents(Character.NONE);

        playableCharacter.addStudent(Color.GREEN);

        assertFalse(playableCharacter.containsAllStudents(List.of(Color.GREEN, Color.GREEN)));

    }

    @Test
    public void containsAllStudents2() {
        PlayableCharacter playableCharacter = new PlayableCharacterWithStudents(Character.NONE);

        playableCharacter.addStudent(Color.GREEN);

        assertTrue(playableCharacter.containsAllStudents(List.of(Color.GREEN)));

    }

    @Test
    public void containsAllStudents3() {
        PlayableCharacter playableCharacter = new PlayableCharacterWithStudents(Character.NONE);

        playableCharacter.addStudent(Color.GREEN);
        playableCharacter.addStudent(Color.GREEN);
        playableCharacter.addStudent(Color.RED);
        assertTrue(playableCharacter.containsAllStudents(List.of(Color.GREEN, Color.RED)));

    }

    @Test
    public void containsAllStudents4() {
        PlayableCharacter playableCharacter = new PlayableCharacterWithStudents(Character.NONE);

        playableCharacter.addStudent(Color.GREEN);
        playableCharacter.addStudent(Color.GREEN);
        playableCharacter.addStudent(Color.RED);
        assertFalse(playableCharacter.containsAllStudents(List.of(Color.RED, Color.RED)));

    }

    @Test
    public void containsAllStudents5() {
        PlayableCharacter playableCharacter = new PlayableCharacterWithStudents(Character.NONE);

        playableCharacter.addStudent(Color.GREEN);
        playableCharacter.addStudent(Color.GREEN);
        playableCharacter.addStudent(Color.RED);
        assertFalse(playableCharacter.containsAllStudents(List.of(Color.CYAN, Color.RED)));

    }
}
