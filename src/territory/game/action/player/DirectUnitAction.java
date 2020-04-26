package territory.game.action.player;

import territory.game.GameColor;
import territory.game.player.Player;

import java.io.Serializable;

public class DirectUnitAction extends PlayerAction implements Serializable {
    private int unitIndex;

    //this will be interpreted based on what kind of target it is
    private int targetIndex;

    public DirectUnitAction(GameColor color, int unitIndex, int targetIndex) {
        super(color);
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
