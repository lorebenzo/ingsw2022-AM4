package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.NonMergeableArchipelagosException;
import org.junit.Test;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ArchipelagoTest {

    @Test
    public void merge1() {
        Archipelago a1 = new Archipelago(1);
        Archipelago a2 = new Archipelago(2);
        Archipelago merged = null;

        a1.addStudent(Color.RED);
        a2.addStudent(Color.GREEN);

        a1.setTowerColor(TowerColor.BLACK);
        a2.setTowerColor(TowerColor.BLACK);

        try {
            merged = Archipelago.merge(a1, a2);
        } catch (NonMergeableArchipelagosException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(merged,a1);

        assertTrue(merged.getIslandCodes().containsAll(a1.getIslandCodes()));
        assertTrue(merged.getIslandCodes().containsAll(a2.getIslandCodes()));

        assertTrue(merged.getStudents().contains(Color.RED));
        assertTrue(merged.getStudents().contains(Color.GREEN));
        assertEquals(2, merged.getStudents().size());

    }

    @Test
    public void merge2() {
        Archipelago a1 = new Archipelago(1);
        Archipelago a2 = new Archipelago(2);
        Archipelago a3 = new Archipelago(3);
        Archipelago merged = null;

        a1.addStudent(Color.RED);
        a2.addStudent(Color.GREEN);
        a3.addStudent(Color.GREEN);

        a1.setTowerColor(TowerColor.BLACK);
        a2.setTowerColor(TowerColor.BLACK);
        a3.setTowerColor(TowerColor.BLACK);


        try {
            merged = Archipelago.merge(a1, a2);
        } catch (NonMergeableArchipelagosException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(merged,a1);

        assertTrue(merged.getIslandCodes().containsAll(a1.getIslandCodes()));
        assertTrue(merged.getIslandCodes().containsAll(a2.getIslandCodes()));
        assertEquals(2, merged.getIslandCodes().size());

        assertTrue(merged.getStudents().contains(Color.RED));
        assertTrue(merged.getStudents().contains(Color.GREEN));
        assertEquals(2, merged.getStudents().size());


        try {
            merged = Archipelago.merge(a1, a3);
        } catch (NonMergeableArchipelagosException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(merged,a1);

        assertTrue(merged.getStudents().contains(Color.RED));
        assertTrue(merged.getStudents().contains(Color.GREEN));
        assertEquals(3, merged.getStudents().size());

        assertTrue(merged.getIslandCodes().containsAll(a1.getIslandCodes()));
        assertTrue(merged.getIslandCodes().containsAll(a2.getIslandCodes()));
        assertTrue(merged.getIslandCodes().containsAll(a3.getIslandCodes()));
        assertEquals(3, merged.getIslandCodes().size());

    }



    @Test
    public void unmergeables() {
        // Arrange
        Archipelago unmergeableA1 = new Archipelago(20);
        Archipelago unmergeableA2 = new Archipelago(21);
        Archipelago unmergeableA3 = new Archipelago(22);
        Archipelago unmergeableA4 = new Archipelago(20);
        Archipelago unmergeableA5 = new Archipelago(30);
        Archipelago unmergeableA6 = new Archipelago(31);
        Archipelago unmergeableA9 = new Archipelago(40);

        //unmergeable archipelagos
        unmergeableA1.setTowerColor(TowerColor.WHITE);
        unmergeableA2.setTowerColor(TowerColor.BLACK);
        unmergeableA3.setTowerColor(TowerColor.GRAY);
        unmergeableA4.setTowerColor(TowerColor.WHITE);
        unmergeableA5.setTowerColor(TowerColor.NONE);
        unmergeableA6.setTowerColor(TowerColor.NONE);
        unmergeableA9.setTowerColor(TowerColor.GRAY);


        //A1 A1
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA1,unmergeableA1));

        //A1 A2
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA1,unmergeableA2));

        //A1 A3
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA1,unmergeableA3));

        //A1 A4
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA1,unmergeableA4));

        //A1 A5
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA1,unmergeableA5));

        //A2

        //A2 A3
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA2,unmergeableA3));

        //A2 A4
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA2,unmergeableA4));

        //A2 A5
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA2,unmergeableA5));

        //A3

        //A3 A4
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA3,unmergeableA4));

        //A3 A5
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA3,unmergeableA5));

        //A4

        //A4 A5
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA4,unmergeableA5));

        //A5

        //A5 A5
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA5,unmergeableA5));

        //A5 A6
        assertThrows(NonMergeableArchipelagosException.class, () -> Archipelago.merge(unmergeableA5,unmergeableA6));

    }

    @Test
    public void getInfluence() {
        // Arrange
        Archipelago archipelago = new Archipelago(55);

        archipelago.setTowerColor(TowerColor.GRAY);
        archipelago.addStudent(Color.PURPLE);
        archipelago.addStudent(Color.RED);
        for(int i = 0; i < 2000; i++) archipelago.addStudent(Color.GREEN);

        // Act
        int influence1 = archipelago.getInfluence(
                Stream.of(Color.RED).collect(Collectors.toSet()),
                TowerColor.WHITE
        );

        int influence2 = archipelago.getInfluence(
                Stream.of(Color.RED).collect(Collectors.toSet()),
                TowerColor.GRAY
        );

        int influence3 = archipelago.getInfluence(
                Stream.of(Color.GREEN, Color.PURPLE, Color.CYAN).collect(Collectors.toSet()),
                TowerColor.GRAY
        );

        int influence4 = archipelago.getInfluence(
                Stream.of(Color.GREEN, Color.PURPLE, Color.CYAN).collect(Collectors.toSet()),
                TowerColor.WHITE
        );

        int influence5 = archipelago.getInfluence(
                new HashSet<>(),
                TowerColor.GRAY
        );

        int influence6 = archipelago.getInfluence(
                new HashSet<>(),
                TowerColor.BLACK
        );

        int influence7 = archipelago.getInfluence(
                Stream.of(Color.YELLOW, Color.CYAN).collect(Collectors.toSet()),
                TowerColor.GRAY
        );

        int influence8 = archipelago.getInfluence(
                Stream.of(Color.YELLOW, Color.CYAN).collect(Collectors.toSet()),
                TowerColor.BLACK
        );

        // Assert
        assertEquals(1, influence1);
        assertEquals(2, influence2);
        assertEquals(2002, influence3);
        assertEquals(2001, influence4);
        assertEquals(1, influence5);
        assertEquals(0, influence6);
        assertEquals(1, influence7);
        assertEquals(0, influence8);
    }
}