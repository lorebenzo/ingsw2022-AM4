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
        Archipelago mergeableA1 = new Archipelago(1);
        Archipelago mergeableA2 = new Archipelago(2);
        Archipelago mergeableA3 = new Archipelago(-445);

        Archipelago unmergeableA1 = new Archipelago(20);
        Archipelago unmergeableA2 = new Archipelago(21);
        Archipelago unmergeableA3 = new Archipelago(22);
        Archipelago unmergeableA4 = new Archipelago(20);

        Archipelago unmergeableA5 = new Archipelago(30);
        Archipelago unmergeableA6 = new Archipelago(31);

        Archipelago mergeableA7 = new Archipelago(40);
        Archipelago mergeableA8 = new Archipelago(41);
        Archipelago unmergeableA9 = new Archipelago(40);

        //Test merge with 3 different mergeable archipelagos
        mergeableA1.setTowerColor(TowerColor.BLACK);
        mergeableA2.setTowerColor(TowerColor.BLACK);
        mergeableA3.setTowerColor(TowerColor.BLACK);

        //unmergeable archipelagos
        unmergeableA1.setTowerColor(TowerColor.WHITE);
        unmergeableA2.setTowerColor(TowerColor.BLACK);
        unmergeableA3.setTowerColor(TowerColor.GRAY);
        unmergeableA4.setTowerColor(TowerColor.WHITE);
        unmergeableA5.setTowerColor(TowerColor.NONE);
        unmergeableA6.setTowerColor(TowerColor.NONE);

        mergeableA7.setTowerColor(TowerColor.GRAY);
        mergeableA8.setTowerColor(TowerColor.GRAY);
        unmergeableA9.setTowerColor(TowerColor.GRAY);

        // Act
        Archipelago merged1 = null;
        Archipelago merged2 = null;
        Archipelago merged3 = null;

        try {
            merged1 = Archipelago.merge(mergeableA1, mergeableA2);
        } catch (NonMergeableArchipelagosException e) {
            e.printStackTrace();
            fail();
        }

        try {
            merged2 = Archipelago.merge(merged1, mergeableA3);
        } catch (NonMergeableArchipelagosException e) {
            e.printStackTrace();
            fail();
        }

        Archipelago unmerged1 = null;
        Archipelago unmerged2 = null;
        Archipelago unmerged3 = null;
        Archipelago unmerged4 = null;
        Archipelago unmerged5 = null;
        Archipelago unmerged6 = null;

        //A1 A1
        try{
            unmerged1 = Archipelago.merge(unmergeableA1,unmergeableA1);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A1 A2
        try{
            unmerged1 = Archipelago.merge(unmergeableA1,unmergeableA2);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A1 A3
        try{
            unmerged1 = Archipelago.merge(unmergeableA1,unmergeableA3);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A1 A4
        try{
            unmerged1 = Archipelago.merge(unmergeableA1,unmergeableA4);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A1 A5
        try{
            unmerged1 = Archipelago.merge(unmergeableA1,unmergeableA5);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A2

        //A2 A3
        try{
            unmerged2 = Archipelago.merge(unmergeableA2,unmergeableA3);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A2 A4
        try{
            unmerged2 = Archipelago.merge(unmergeableA2,unmergeableA4);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A2 A5
        try{
            unmerged2 = Archipelago.merge(unmergeableA2,unmergeableA5);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A3

        //A3 A4
        try{
            unmerged3 = Archipelago.merge(unmergeableA3,unmergeableA4);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A3 A5
        try{
            unmerged3 = Archipelago.merge(unmergeableA3,unmergeableA5);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A4

        //A4 A5
        try{
            unmerged4 = Archipelago.merge(unmergeableA4,unmergeableA5);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A5

        //A5 A5
        try{
            unmerged5 = Archipelago.merge(unmergeableA1,unmergeableA5);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        //A5 A6
        try{
            unmerged5 = Archipelago.merge(unmergeableA5,unmergeableA6);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }

        try {
            merged3 = Archipelago.merge(mergeableA7, mergeableA8);
        } catch (NonMergeableArchipelagosException e) {
            e.printStackTrace();
            fail();
        }

        try {
            unmerged6 = Archipelago.merge(merged3, unmergeableA9);
        }catch (NonMergeableArchipelagosException e){
            e.printStackTrace();
        }


        // Assert

        assertTrue(merged1.getIslandCodes().containsAll(mergeableA1.getIslandCodes()));
        assertTrue(merged1.getIslandCodes().containsAll(mergeableA2.getIslandCodes()));
        assertEquals(2, merged1.getIslandCodes().size());

        assertTrue(merged2.getIslandCodes().containsAll(merged1.getIslandCodes()));
        assertTrue(merged2.getIslandCodes().containsAll(mergeableA3.getIslandCodes()));
        assertEquals(3, merged2.getIslandCodes().size());

        assertNull(unmerged1);
        assertNull(unmerged2);
        assertNull(unmerged3);
        assertNull(unmerged4);
        assertNull(unmerged5);
        assertNull(unmerged6);

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