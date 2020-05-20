package territory.game.construction;

import territory.game.sprite.ImageStore;
import territory.game.target.BuildSlot;
import territory.game.target.Buildable;

import java.io.Serializable;

public class WallSegment extends Buildable implements Construction, Serializable {

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
    public double getBuildZoneRadius(){
        return ImageStore.store.imageFor(this, color).getWidth();
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
