package game.action;

import game.construction.Buildable;
import game.player.Player;

public class PlaceStoneAction extends TickAction {
    private Buildable buildable;
    private int stone;

    public PlaceStoneAction(Player player, Buildable buildable, int stone) {
        super(player);
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
