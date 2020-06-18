package territory.game.target;

import territory.game.construction.Mine;

import java.io.Serializable;

/**
 * Represents a location that a Miner can mine from
 */
public class MineSlot extends Slot implements Serializable {
    private Mine mine;

    public MineSlot(Mine mine, double x, double y){
        super(x, y);
        this.mine = mine;
    }

    public Mine getMine() {
        return mine;
    }

}
