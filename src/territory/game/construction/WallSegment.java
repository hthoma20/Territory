package territory.game.construction;

import territory.game.target.BuildSlot;
import territory.game.target.Buildable;

import java.io.Serializable;

public class WallSegment extends Buildable implements Serializable {

    private Wall wall;

    public WallSegment(Wall wall, double x, double y, double rotation) {
        super(wall.getColor(), x, y);

        this.rotation = rotation;
        this.wall = wall;
    }

    public WallSegment(WallSegment src) {
        super(src);

        this.wall = src.wall;
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

    public Wall getWall() {
        return wall;
    }
}
