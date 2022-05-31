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

    @Override
    public void lock() throws ArchipelagoAlreadyLockedException {
        if(!this.lock)
            this.lock = true;
        else
            throw new ArchipelagoAlreadyLockedException();
    }

    @Override
    public void unlock(){
        this.lock = false;
    }

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

    @Override
    public void setTowersInfluence(boolean doTowersCount){
        this.doTowersCount = doTowersCount;
    }

    @Override
    public void setColorThatDoesntCount(Color color){
        this.colorThatDoesntCount = color;
    }

    @Override
    protected int getTowersInfluence(TowerColor playerTowerColor) {
        if(doTowersCount)
            return super.getTowersInfluence(playerTowerColor);

        return 0;
    }

    @Override
    protected int getStudentsInfluence(Set<Color> playerProfessors) {
        Set<Color> playerProfessorsWithoutColor = new HashSet<>(playerProfessors);

        playerProfessorsWithoutColor.remove(colorThatDoesntCount);
        return super.getStudentsInfluence(playerProfessorsWithoutColor);
    }


    /**
     * //@throws NonMergeableArchipelagosException if the two archipelagos have towers of different colors or
     * if the two archipelagos have intersecting islandCodes
     *
     * @param a2 second archipelago
     * @return an Archipelago that has first islandCodes + second islandCodes
     */
    @Override
    public boolean merge(Archipelago a2) {
        this.lock = this.lock || a2.isLocked();
        return super.merge(a2);
    }
}
