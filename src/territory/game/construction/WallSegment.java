package territory.game.construction;

import territory.game.player.Player;

public class WallSegment extends Buildable {


    public WallSegment(Player owner, double x, double y, double rotation) {
        super(owner, x, y);

        this.rotation = rotation;
    }

    public WallSegment(WallSegment src) {
        super(src);
    }

    @Override
    public WallSegment copy() {
        return new WallSegment(this);
    }

    @Override
    protected BuildSlot[] initSlots() {
        return new BuildSlot[]{
            new BuildSlot(this, x, y)
        };
    }
}
