package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;

import java.util.*;

public class SchoolBoard implements Cloneable {
    private final int id;
    private final Map<Color, Integer> diningRoomLaneColorToNumberOfStudents;
    private final TowerColor towerColor;
    private final List<Color> studentsInTheEntrance;
    private final Set<Color> professorsTable;
    private final List<Card> deck;

    public SchoolBoard(int id, List<Color> studentsInTheEntrance, TowerColor towerColor) {
        this.id = id;
        this.studentsInTheEntrance = studentsInTheEntrance;
        this.towerColor = towerColor;

        this.diningRoomLaneColorToNumberOfStudents = new HashMap<>();
        this.professorsTable = new HashSet<>();
        this.deck = new LinkedList<>();

        for(Color color : Color.values())
            this.diningRoomLaneColorToNumberOfStudents.put(color, 0);

        // Push all the 10 cards into the deck
        this.deck.addAll(Arrays.asList(Card.values()).subList(0, 10));
    }

    private SchoolBoard(int id,
                        Map<Color, Integer> diningRoomLaneColorToNumberOfStudents,
                        TowerColor towerColor,
                        List<Color> studentsInTheEntrance,
                        Set<Color> professorsTable,
                        List<Card> deck) {
        this.id = id;
        this.diningRoomLaneColorToNumberOfStudents = diningRoomLaneColorToNumberOfStudents;
        this.towerColor = towerColor;
        this.studentsInTheEntrance = studentsInTheEntrance;
        this.professorsTable = professorsTable;
        this.deck = deck;
    }

    @Override
    public Object clone() {
        return new SchoolBoard(
                this.id,
                new HashMap<>(this.diningRoomLaneColorToNumberOfStudents),
                this.towerColor,
                new ArrayList<>(this.studentsInTheEntrance),
                new HashSet<>(professorsTable),
                new LinkedList<>(this.deck)
        );
    }
}
