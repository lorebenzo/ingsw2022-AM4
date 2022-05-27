package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.model.game_logic.exceptions.ArchipelagoAlreadyLockedException;

public class ExpertArchipelago extends Archipelago{
    private boolean lock;
    private boolean doTowersCount;

    public ExpertArchipelago(int code) {
        super(code);
        this.lock = false;
        this.doTowersCount = true;
    }

    public void lock() throws ArchipelagoAlreadyLockedException {
        if(!this.lock)
            this.lock = true;
        else
            throw new ArchipelagoAlreadyLockedException();
    }

    public void unlock(){
        this.lock = false;
    }

    public void setIfTowersDoCount(boolean doTowersCount){
        this.doTowersCount = doTowersCount;
    }

    @Override
    protected int getTowersInfluence(TowerColor playerTowerColor) {
        if(doTowersCount)
            return super.getTowersInfluence(playerTowerColor);
        else
            return 0;
    }
}
