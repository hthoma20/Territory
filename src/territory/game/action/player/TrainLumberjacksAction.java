package territory.game.action.player;

import territory.game.GameColor;

import java.io.Serializable;

public class TrainLumberjacksAction extends TrainUnitsAction implements Serializable {
    public TrainLumberjacksAction(GameColor color, int villageIndex, int numUnits) {
        super(color, villageIndex, numUnits);
    }
}
