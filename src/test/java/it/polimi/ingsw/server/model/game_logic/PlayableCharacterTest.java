package it.polimi.ingsw.server.model.game_logic;


import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void lightify() {
        PlayableCharacter playableCharacter = PlayableCharacter.createCharacter(Character.TOWERS_DONT_COUNT);

        LightPlayableCharacter lightPlayableCharacter = playableCharacter.lightify();

        assertEquals(playableCharacter.getCharacterId(), lightPlayableCharacter.characterId);
        assertEquals(playableCharacter.initialCost, lightPlayableCharacter.initialCost);
        assertEquals(playableCharacter.getCurrentCost(), lightPlayableCharacter.currentCost);
        assertEquals(playableCharacter.effect, lightPlayableCharacter.effect);
        assertNull(playableCharacter.getProfessorToOriginalOwnerMap());
        assertFalse(playableCharacter.isLockAvailable());
        assertNull(playableCharacter.getStudents());
        assertNull(lightPlayableCharacter.students);

    }
    @Test
    public void lightify2() {
        PlayableCharacter playableCharacter = PlayableCharacter.createCharacter(Character.GET_PROFESSORS_WITH_SAME_STUDENTS);

        LightPlayableCharacter lightPlayableCharacter = playableCharacter.lightify();

        assertEquals(playableCharacter.getCharacterId(), lightPlayableCharacter.characterId);
        assertEquals(playableCharacter.initialCost, lightPlayableCharacter.initialCost);
        assertEquals(playableCharacter.getCurrentCost(), lightPlayableCharacter.currentCost);
        assertEquals(playableCharacter.effect, lightPlayableCharacter.effect);
        assertEquals(playableCharacter.getProfessorToOriginalOwnerMap(), lightPlayableCharacter.professorToOriginalOwnerMap);
        assertFalse(playableCharacter.isLockAvailable());
        assertNull(playableCharacter.getStudents());
        assertNull(lightPlayableCharacter.students);

    }
    @Test
    public void lightify3() {
        PlayableCharacter playableCharacter = PlayableCharacter.createCharacter(Character.LOCK_ARCHIPELAGO);

        LightPlayableCharacter lightPlayableCharacter = playableCharacter.lightify();

        assertEquals(playableCharacter.getCharacterId(), lightPlayableCharacter.characterId);
        assertEquals(playableCharacter.initialCost, lightPlayableCharacter.initialCost);
        assertEquals(playableCharacter.getCurrentCost(), lightPlayableCharacter.currentCost);
        assertEquals(playableCharacter.effect, lightPlayableCharacter.effect);
        assertNull(playableCharacter.getProfessorToOriginalOwnerMap());
        assertTrue(playableCharacter.isLockAvailable());
        assertNull(playableCharacter.getStudents());
        assertNull(lightPlayableCharacter.students);

    }
    @Test
    public void lightify4() {

        PlayableCharacter playableCharacter = PlayableCharacter.createCharacter(Character.SWAP_THREE_STUDENTS_BETWEEN_CHARACTER_AND_ENTRANCE);

        playableCharacter.addStudent(Color.RED);

        LightPlayableCharacter lightPlayableCharacter = playableCharacter.lightify();

        assertEquals(playableCharacter.getCharacterId(), lightPlayableCharacter.characterId);
        assertEquals(playableCharacter.initialCost, lightPlayableCharacter.initialCost);
        assertEquals(playableCharacter.getCurrentCost(), lightPlayableCharacter.currentCost);
        assertEquals(playableCharacter.effect, lightPlayableCharacter.effect);
        assertNull(playableCharacter.getProfessorToOriginalOwnerMap());
        assertFalse(playableCharacter.isLockAvailable());

        assertTrue(playableCharacter.getStudents().containsAll(lightPlayableCharacter.students));
        assertTrue(lightPlayableCharacter.students.containsAll(playableCharacter.getStudents()));
        assertEquals(playableCharacter.getStudents().size(), lightPlayableCharacter.students.size());
        assertTrue(playableCharacter.getStudents().size() > 0);


    }
}
