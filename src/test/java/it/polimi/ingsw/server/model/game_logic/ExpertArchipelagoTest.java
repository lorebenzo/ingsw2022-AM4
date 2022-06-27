package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.ArchipelagoAlreadyLockedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ExpertArchipelagoTest {

    @Test
    public void lockTest() throws ArchipelagoAlreadyLockedException {
        Archipelago archipelago = new ExpertArchipelago(1);
        Assertions.assertFalse(archipelago.isLocked());
        archipelago.lock();
        Assertions.assertTrue(archipelago.isLocked());
        assertThrows(ArchipelagoAlreadyLockedException.class, archipelago::lock);
        archipelago.unlock();
        Assertions.assertFalse(archipelago.isLocked());
    }

    @Test
    public void merge() {
        // Arrange
        Archipelago mergeableA1 = new ExpertArchipelago(1);
        Archipelago mergeableA2 = new ExpertArchipelago(2);

        //Test merge with 3 different mergeable archipelagos
        mergeableA1.setTowerColor(TowerColor.BLACK);
        mergeableA2.setTowerColor(TowerColor.BLACK);

        // Act
        mergeableA1.merge(mergeableA2);

        // Assert

        assertTrue(mergeableA1.getIslandCodes().containsAll(mergeableA1.getIslandCodes()));
        assertTrue(mergeableA1.getIslandCodes().containsAll(mergeableA2.getIslandCodes()));
        assertEquals(2, mergeableA1.getIslandCodes().size());

    }

    @Test
    public void mergeWithLocks() throws ArchipelagoAlreadyLockedException {
        Archipelago a1 = new ExpertArchipelago(1);
        Archipelago a2 = new ExpertArchipelago(2);

        a1.lock();

        a1.setTowerColor(TowerColor.BLACK); //This will not set the towercolor because the archipelago is locked
        a2.setTowerColor(TowerColor.BLACK);
        assertFalse(a1.merge(a2));

        a1.unlock();

        a1.setTowerColor(TowerColor.BLACK);
        assertTrue(a1.merge(a2));
        assertTrue(a1.getIslandCodes().contains(1));
        assertTrue(a1.getIslandCodes().containsAll(a2.getIslandCodes()));
        assertEquals(2, a1.getIslandCodes().size());

    }

    @Test
    public void getInfluenceTowersDontCount() {
        Archipelago archipelago = new ExpertArchipelago(1);
        archipelago.setTowerColor(TowerColor.BLACK);
        archipelago.addStudent(Color.PURPLE);
        archipelago.addStudent(Color.RED);

        Set<Color> playerProfessors = new HashSet<>(Set.of(Color.RED, Color.PURPLE));

        assertEquals(3, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));

        archipelago.setTowersInfluence(false);
        assertEquals(2, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));

        archipelago.setTowersInfluence(true);
        assertEquals(3, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));
    }

    @Test
    public void getInfluenceTowersDontCount2() {
        Archipelago archipelago = new ExpertArchipelago(1);
        Archipelago archipelago2 = new ExpertArchipelago(2);


        archipelago.setTowerColor(TowerColor.BLACK);
        archipelago.addStudent(Color.PURPLE);
        archipelago.addStudent(Color.RED);

        archipelago2.setTowerColor(TowerColor.BLACK);
        archipelago2.addStudent(Color.PURPLE);
        archipelago2.addStudent(Color.RED);

        archipelago.merge(archipelago2);

        Set<Color> playerProfessors = new HashSet<>(Set.of(Color.RED, Color.PURPLE));

        assertEquals(6, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));

        archipelago.setTowersInfluence(false);
        assertEquals(4, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));

        archipelago.setTowersInfluence(true);
        assertEquals(6, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));
    }

    @Test
    public void getInfluenceColorDoesntCount() {
        Archipelago archipelago = new ExpertArchipelago(1);
        archipelago.setTowerColor(TowerColor.BLACK);
        archipelago.addStudent(Color.PURPLE);
        archipelago.addStudent(Color.RED);

        Set<Color> playerProfessors = new HashSet<>(Set.of(Color.RED, Color.PURPLE));

        assertEquals(3, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));

        archipelago.setColorThatDoesntCount(Color.RED);
        assertEquals(2, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));

        archipelago.setColorThatDoesntCount(null);
        assertEquals(3, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));
    }

    @Test
    public void getInfluenceColorDoesntCount2() {
        Archipelago archipelago = new ExpertArchipelago(1);
        Archipelago archipelago2 = new ExpertArchipelago(2);

        archipelago.setTowerColor(TowerColor.BLACK);
        archipelago.addStudent(Color.PURPLE);
        archipelago.addStudent(Color.RED);

        archipelago2.setTowerColor(TowerColor.BLACK);
        archipelago2.addStudent(Color.PURPLE);
        archipelago2.addStudent(Color.RED);

        archipelago.merge(archipelago2);

        Set<Color> playerProfessors = new HashSet<>(Set.of(Color.RED, Color.PURPLE));

        assertEquals(6, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));

        archipelago.setColorThatDoesntCount(Color.RED);
        assertEquals(4, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));

        archipelago.setColorThatDoesntCount(null);
        assertEquals(6, archipelago.getInfluence(playerProfessors,TowerColor.BLACK));
    }

    @Test
    public void lightify() throws ArchipelagoAlreadyLockedException {
        Archipelago archipelago = new ExpertArchipelago(0);

        archipelago.addStudent(Color.RED);
        archipelago.addStudent(Color.RED);
        archipelago.addStudent(Color.GREEN);

        archipelago.setTowerColor(TowerColor.BLACK);

        archipelago.setColorThatDoesntCount(Color.RED);
        archipelago.lock();
        archipelago.setTowersInfluence(false);

        LightArchipelago lightArchipelago = archipelago.lightify();

        assertEquals(archipelago.getIslandCodes(), lightArchipelago.islandCodes);
        assertEquals(archipelago.getStudentToNumber(), lightArchipelago.studentToNumber);
        assertEquals(archipelago.getTowerColor(), lightArchipelago.towerColor);

    }
}
