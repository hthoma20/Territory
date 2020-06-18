package territory.game.target;

import territory.game.construction.Buildable;

import java.io.Serializable;

/**
 * Represents a location that a Miner can mine from
 */
public class BuildSlot extends Slot implements Serializable {
    private Buildable buildable;

    public BuildSlot(Buildable buildable, double x, double y){
        super(x, y);
        this.buildable = buildable;
    }

    public Buildable getBuildable() {
        return buildable;
    }

}
