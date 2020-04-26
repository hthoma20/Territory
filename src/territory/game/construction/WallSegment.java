package territory.game.construction;

import territory.game.GameColor;
import territory.game.player.Player;

import java.io.Serializable;

public class WallSegment extends Buildable implements Serializable {


    public WallSegment(GameColor color, double x, double y, double rotation) {
        super(color, x, y);

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
