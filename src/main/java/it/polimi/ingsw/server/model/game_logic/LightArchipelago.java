package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;

import java.util.List;
import java.util.Map;

public class LightArchipelago {
    public final List<Integer> islandCodes;
    public final Map<Color, Integer> studentToNumber;
    public final TowerColor towerColor;
    public final boolean lock;
    public final boolean doTowersCount;
    public final Color colorThatDoesntCount;

    public LightArchipelago(List<Integer> islandCodes, Map<Color, Integer> studentToNumber, TowerColor towerColor, boolean lock, boolean doTowersCount, Color colorThatDoesntCount) {
        this.islandCodes = islandCodes;
        this.studentToNumber = studentToNumber;
        this.towerColor = towerColor;
        this.lock = lock;
        this.doTowersCount = doTowersCount;
        this.colorThatDoesntCount = colorThatDoesntCount;
    }
}
