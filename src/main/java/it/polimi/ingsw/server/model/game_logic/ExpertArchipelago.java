package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.ArchipelagoAlreadyLockedException;

import java.util.HashSet;
import java.util.Set;

public class ExpertArchipelago extends Archipelago{
    private boolean lock;
    private boolean doTowersCount;
    private Color colorThatDoesntCount;

    public ExpertArchipelago(int code) {
        super(code);
        this.lock = false;
        this.doTowersCount = true;
    }

    /**
     * This method sets the lock of the archipelago to true
     * @throws ArchipelagoAlreadyLockedException if the method is called while the lock is already set to true
     */
    @Override
    public void lock() throws ArchipelagoAlreadyLockedException {
        if(!this.lock)
            this.lock = true;
        else
            throw new ArchipelagoAlreadyLockedException();
    }

    /**
     * This method sets the lock of the archipelago to false
     */
    @Override
    public void unlock(){
        this.lock = false;
    }

    /**
     * This method returns true if the archipelago's locks are set to true, false otherwise
     * @return the state of the lock of the archipelago
     */
    @Override
    public boolean isLocked() {
        return this.lock;
    }

    /**
     * Sets the towerColor of this archipelago
     *
     * @param towerColor towercolor
     * @throws IllegalArgumentException if(towerColor == null)
     */
    @Override
    public void setTowerColor(TowerColor towerColor) {
        if(!this.isLocked())
            super.setTowerColor(towerColor);
    }

    /**
     * This method sets if the towers will count for the influence
     * @param doTowersCount true if the towers have to count, false otherwise
     */
    @Override
    public void setTowersInfluence(boolean doTowersCount){
        this.doTowersCount = doTowersCount;
    }

    /**
     * This method sets the color that won't count for the influence
     * @param color is the color that won't count
     */
    @Override
    public void setColorThatDoesntCount(Color color){
        this.colorThatDoesntCount = color;
    }

    /**
     * This method returns the influence given by the towers of the inputted tower color on the archipelago
     * @param playerTowerColor is the tower color
     * @return the influence given by the towers of a certain color on the archipelago
     */
    @Override
    protected int getTowersInfluence(TowerColor playerTowerColor) {
        if(doTowersCount)
            return super.getTowersInfluence(playerTowerColor);

        return 0;
    }

    /**
     * This method returns the influence on the archipelago given by the students that are on it
     * @param playerProfessors is a Set representing the professors owned by a certain player
     * @return the influence on the archipelago given by the students
     */
    @Override
    protected int getStudentsInfluence(Set<Color> playerProfessors) {
        Set<Color> playerProfessorsWithoutColor = new HashSet<>(playerProfessors);

        playerProfessorsWithoutColor.remove(colorThatDoesntCount);
        return super.getStudentsInfluence(playerProfessorsWithoutColor);
    }


    /**
     * This method merges two archipelagos
     * @param a2 second archipelago to be merged. If one of the two archipelagos is locked, the lock will affect the merged archipelago
     * @return an Archipelago that has first islandCodes + second islandCodes and the sum of the students of the two archipelagos
     */
    @Override
    public boolean merge(Archipelago a2) {
        this.lock = this.lock || a2.isLocked();
        return super.merge(a2);
    }

    /**
     * This method returns the light version of the archipelago to be sent over the network
     * @return a LightArchipelago comprising all the relevant information that make up the archipelago
     */
    @Override
    public LightArchipelago lightify() {
            return new LightArchipelago(
                    super.islandCodes,
                    super.studentToNumber,
                    super.towerColor,
                    this.lock,
                    this.doTowersCount,
                    this.colorThatDoesntCount);
    }
}
