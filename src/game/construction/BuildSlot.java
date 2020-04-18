package game.construction;

/**
 * Represents a location that a Miner can mine from
 */
public class BuildSlot extends Slot {
    private Buildable buildable;

    public BuildSlot(Buildable buildable, double x, double y){
        super(x, y);
        this.buildable = buildable;
    }

    public BuildSlot(BuildSlot src){
        super(src);

        this.buildable = src.buildable;
    }

    @Override
    public BuildSlot copy(){
        return new BuildSlot(this);
    }

    public Buildable getBuildable() {
        return buildable;
    }

}
