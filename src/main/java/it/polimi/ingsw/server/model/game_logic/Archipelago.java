package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.NonMergeableArchipelagosException;

import java.io.Serializable;
import java.util.*;

/**
 * This class generalizes the concept of island:
 * an island is an archipelago with a single island and
 * multiple archipelagos merge into a single archipelago
 */
public class Archipelago implements ArchipelagoCommonInterface {
    protected final List<Integer> islandCodes;
    protected final Map<Color, Integer> studentToNumber;
    protected TowerColor towerColor;

    private Archipelago() {
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
     * //@throws NonMergeableArchipelagosException if the two archipelagos have towers of different colors or
     *                                              if the two archipelagos have intersecting islandCodes
     * @param a2 second archipelago
     * @return an Archipelago that has first islandCodes + second islandCodes
     */
    public boolean merge(Archipelago a2) {
        if(a2 == null) throw new IllegalArgumentException();

        if(this.towerColor.equals(a2.towerColor) && !this.towerColor.equals(TowerColor.NONE) && this.islandCodes.stream().noneMatch(a2.islandCodes::contains)){
            this.islandCodes.addAll(a2.islandCodes);
            this.islandCodes.sort(Comparator.naturalOrder());
            this.studentToNumber.replaceAll((k, v) -> this.studentToNumber.get(k) + a2.studentToNumber.get(k));
            return true;
        }
        return false;
    }

    public List<Color> getStudents() {
        var students = new LinkedList<Color>();
        for(var key : this.studentToNumber.keySet())
            for(int i = 0; i < this.studentToNumber.get(key); i++)
                students.add(key);
        return students;
    }

    /**
     * Adds a student to the archipelago
     * @throws IllegalArgumentException if(student == null)
     * @param student any student
     */
    public void addStudent(Color student) {
        if(student == null) throw new IllegalArgumentException();
        this.studentToNumber.put(student, this.studentToNumber.get(student) + 1);
    }

    /**
     * @throws IllegalArgumentException if the arguments representing the set of professors owned by the player is null or
     *                                        playerProfessors contains null or
     *                                        the argument representing the color of the player's tower is null or
     *                                        the argument representing the color of the player's tower is TowerColor.NONE
     * @param   playerProfessors a set containing all the color of the professors owned by the player
     * @param   playerTowerColor the color of the towers owned by the player
     * @return  an int representing the value of the player's influence on this archipelago
     */
    public /* pure */ int getInfluence(Set<Color> playerProfessors, TowerColor playerTowerColor) {
        if(
                playerProfessors == null || playerProfessors.contains(null) ||
                playerTowerColor == null || playerTowerColor.equals(TowerColor.NONE)
        ) throw new IllegalArgumentException();
        return getStudentsInfluence(playerProfessors)
                + getTowersInfluence(playerTowerColor); // add tower score
    }

    protected int getTowersInfluence(TowerColor playerTowerColor){
        return (playerTowerColor.equals(this.towerColor)) ? this.islandCodes.size() : 0;
    }

    protected int getStudentsInfluence(Set<Color> playerProfessors){
        return this.studentToNumber.keySet().stream()        // get student colors
                .filter(playerProfessors::contains)     // filter the ones that match given professors colors
                .mapToInt(this.studentToNumber::get)    // map each student to the number of occurrences in this archipelago
                .sum();                                  // sum the occurrences
    }

    public List<Integer> getIslandCodes() {
        return new ArrayList<>(this.islandCodes);
    }

    /**
     * Sets the towerColor of this archipelago
     * @throws IllegalArgumentException if(towerColor == null)
     */
    public void setTowerColor(TowerColor towerColor) {
        if(towerColor == null) throw new IllegalArgumentException();
        this.towerColor = towerColor;
    }

    public TowerColor getTowerColor(){
        return this.towerColor;
    }

    public void removeStudent(Color student){
        if(this.studentToNumber.get(student) >= 1)
            this.studentToNumber.put(student, this.studentToNumber.get(student) -1);
    }

    public LightArchipelago lightify(){
        return new LightArchipelago(
                this.islandCodes,
                this.studentToNumber,
                this.towerColor,
                false,
                true,
                null);
    }
}
