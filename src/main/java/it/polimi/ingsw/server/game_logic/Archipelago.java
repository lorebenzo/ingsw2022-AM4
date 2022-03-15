package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.NonMergeableArchipelagosException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class generalizes the concept of island:
 * an island is an archipelago with a single island and
 * multiple arhipelagos merge into a single archipelago
 */
public class Archipelago {
    private List<Integer> islandCodes;
    private Map<Color, Integer> studentToNumber;
    private TowerColor towerColor;

    public Archipelago() {
        this.islandCodes = new ArrayList<>();
        this.studentToNumber = new HashMap<>(Color.values().length);

        for(Color c : Color.values())
            this.studentToNumber.put(c, 0);
        this.towerColor = TowerColor.NONE;
    }

    public Archipelago(int code) {
        this.islandCodes = new ArrayList<>(1);
        this.islandCodes.add(code);
        this.studentToNumber = new HashMap<>(Color.values().length);

        for(Color c : Color.values())
            this.studentToNumber.put(c, 0);
        this.towerColor = TowerColor.NONE;
    }

    private Archipelago(List<Integer> islandCodes1, List<Integer> islandCodes2,
                        Map<Color, Integer> studentToNumber1, Map<Color, Integer> studentToNumber2,
                        TowerColor towerColor) {
        this();

        this.islandCodes.addAll(islandCodes1);
        this.islandCodes.addAll(islandCodes2);

        this.studentToNumber.replaceAll((k, v) -> studentToNumber1.get(k) + studentToNumber2.get(k));

        this.towerColor = towerColor;
    }

    /**
     *
     * @param a1 first archipelago to merge
     * @param a2 second archipelago
     * @return an Archipelago that has first islandCodes + second islandCodes
     */
    public static Archipelago merge(Archipelago a1, Archipelago a2) throws NonMergeableArchipelagosException {
        if(!a1.towerColor.equals(a2.towerColor) || a1.towerColor.equals(TowerColor.NONE))
            throw new NonMergeableArchipelagosException();

        return new Archipelago(
                a1.islandCodes, a2.islandCodes,
                a1.studentToNumber, a2.studentToNumber,
                a1.towerColor
        );
    }

    public List<Integer> getIslandCodes() {
        return new ArrayList<>(this.islandCodes);
    }

    public void setTowerColor(TowerColor towerColor) {
        this.towerColor = towerColor;
    }
}
