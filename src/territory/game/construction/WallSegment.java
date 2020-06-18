package territory.game.construction;

import territory.game.target.BuildSlot;

import java.io.Serializable;

public class WallSegment extends Buildable implements Construction, Serializable {

    private Wall wall;

    public WallSegment(Wall wall, double x, double y, double rotation) {
        super(wall.getColor(), x, y);

        this.rotation = rotation;
        this.wall = wall;
    }

    @Override
    public double getBuildZoneRadius(){
        return getWidth();
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
