package territory.game.action.tick;

import territory.game.GameColor;
import territory.game.construction.Buildable;

public class PlaceStoneAction extends TickAction {
    private Buildable buildable;
    private int stone;

    public PlaceStoneAction(GameColor color, Buildable buildable, int stone) {
        super(color);
        this.buildable = buildable;
        this.stone = stone;
    }

    public Buildable getBuildable() {
        return buildable;
    }

    public int getStone() {
        return stone;
    }
}
