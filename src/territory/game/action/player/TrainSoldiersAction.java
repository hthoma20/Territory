package territory.game.action.player;

import territory.game.GameColor;

import java.io.Serializable;

public class TrainSoldiersAction extends TrainUnitsAction implements Serializable {
    public TrainSoldiersAction(GameColor color, int villageIndex, int numUnits) {
        super(color, villageIndex, numUnits);
    }
}
