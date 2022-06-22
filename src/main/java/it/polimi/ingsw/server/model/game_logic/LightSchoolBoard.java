package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LightSchoolBoard {
    public final int id; // must be unique for each GameState
    public final Map<Color, Integer> diningRoomLaneColorToNumberOfStudents;
    public final TowerColor towerColor;
    public final List<Color> studentsInTheEntrance;
    public final Set<Color> professorsTable;
    public final List<Card> deck;
    public final Integer coins;

    public LightSchoolBoard(int id, Map<Color, Integer> diningRoomLaneColorToNumberOfStudents, TowerColor towerColor, List<Color> studentsInTheEntrance, Set<Color> professorsTable, List<Card> deck, Integer coins) {
        this.id = id;
        this.diningRoomLaneColorToNumberOfStudents = diningRoomLaneColorToNumberOfStudents;
        this.towerColor = towerColor;
        this.studentsInTheEntrance = studentsInTheEntrance;
        this.professorsTable = professorsTable;
        this.deck = deck;
        this.coins = coins;
    }
}
