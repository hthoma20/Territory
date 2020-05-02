package territory.game.action.player;

import territory.game.GameColor;

import java.io.Serializable;

public class DirectSoldierAction extends DirectUnitAction implements Serializable {

    private GameColor targetColor;

    public DirectSoldierAction(GameColor color, int unitIndex, GameColor targetColor, int targetIndex) {
        super(color, unitIndex, targetIndex);

        this.targetColor = targetColor;
    }

    public GameColor getTargetColor() {
        return targetColor;
    }
}
