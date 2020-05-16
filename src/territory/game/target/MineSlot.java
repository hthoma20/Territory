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

    public MineSlot(MineSlot src){
        super(src);

        this.mine = src.mine;
    }

    @Override
    public MineSlot copy(){
        return new MineSlot(this);
    }

    public Mine getMine() {
        return mine;
    }

}
