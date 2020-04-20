package game.action;

import game.player.Player;

public class DirectUnitAction extends PlayerAction {
    private int unitIndex;

    //this will be interpreted based on what kind of target it is
    private int targetIndex;

    public DirectUnitAction(Player player, int unitIndex, int targetIndex) {
        super(player);
        this.unitIndex = unitIndex;
        this.targetIndex = targetIndex;
    }

    public int getUnitIndex() {
        return unitIndex;
    }

    public int getTargetIndex() {
        return targetIndex;
    }
}
