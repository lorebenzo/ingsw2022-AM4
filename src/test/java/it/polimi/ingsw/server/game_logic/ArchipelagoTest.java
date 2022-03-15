package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.NonMergeableArchipelagosException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArchipelagoTest {

    @Test
    public void merge() throws NonMergeableArchipelagosException {
        // Arrange
        Archipelago firstArchipelago = new Archipelago(1);
        Archipelago secondArchipelago = new Archipelago(2);

        firstArchipelago.setTowerColor(TowerColor.BLACK);
        secondArchipelago.setTowerColor(TowerColor.BLACK);

        // Act
        Archipelago newArchipelago = Archipelago.merge(firstArchipelago, secondArchipelago);

        // Assert
        assertTrue(newArchipelago.getIslandCodes().containsAll(firstArchipelago.getIslandCodes()));
        assertTrue(newArchipelago.getIslandCodes().containsAll(secondArchipelago.getIslandCodes()));
        assertEquals(2, firstArchipelago.getIslandCodes().size() + secondArchipelago.getIslandCodes().size());
    }
}