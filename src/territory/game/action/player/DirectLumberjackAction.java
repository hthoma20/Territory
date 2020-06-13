package territory.game.action.player;

import territory.game.GameColor;

import java.io.Serializable;

public class DirectLumberjackAction extends DirectUnitAction implements Serializable {

    public DirectLumberjackAction(GameColor color, int unitIndex, int targetIndex) {
        super(color, unitIndex, targetIndex);
    }
}
