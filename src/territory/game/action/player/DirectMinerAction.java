package territory.game.action.player;

import territory.game.GameColor;

import java.io.Serializable;

public class DirectMinerAction extends DirectUnitAction implements Serializable {

    public DirectMinerAction(GameColor color, int unitIndex, int targetIndex) {
        super(color, unitIndex, targetIndex);
    }
}
