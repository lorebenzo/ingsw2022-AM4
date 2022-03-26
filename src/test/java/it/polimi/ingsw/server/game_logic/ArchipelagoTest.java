package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.NonMergeableArchipelagosException;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ArchipelagoTest {

    @Test
    public void merge() {
        // Arrange
        Archipelago firstArchipelago = new Archipelago(1);
        Archipelago secondArchipelago = new Archipelago(2);
        Archipelago thirdArchipelago = new Archipelago(-445);

        Archipelago fourthArchipelago = new Archipelago(20);

        firstArchipelago.setTowerColor(TowerColor.BLACK);
        secondArchipelago.setTowerColor(TowerColor.BLACK);
        thirdArchipelago.setTowerColor(TowerColor.BLACK);

        fourthArchipelago.setTowerColor(TowerColor.WHITE);

        // Act
        Archipelago newArchipelago1 = null;
        Archipelago newArchipelago2 = null;
        try {
            newArchipelago1 = Archipelago.merge(firstArchipelago, secondArchipelago);
            newArchipelago2 = Archipelago.merge(newArchipelago1, thirdArchipelago);
        } catch (NonMergeableArchipelagosException e) {
            e.printStackTrace();
            fail();
        }

        Archipelago newArchipelago3 = null;

        try{
            newArchipelago3 = Archipelago.merge(fourthArchipelago,fourthArchipelago);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        // Assert
        assertTrue(newArchipelago1.getIslandCodes().containsAll(firstArchipelago.getIslandCodes()));
        assertTrue(newArchipelago1.getIslandCodes().containsAll(secondArchipelago.getIslandCodes()));
        assertEquals(2, newArchipelago1.getIslandCodes().size());

        assertTrue(newArchipelago2.getIslandCodes().containsAll(newArchipelago1.getIslandCodes()));
        assertTrue(newArchipelago2.getIslandCodes().containsAll(thirdArchipelago.getIslandCodes()));
        assertEquals(3, newArchipelago2.getIslandCodes().size());

        assertNull(newArchipelago3);
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