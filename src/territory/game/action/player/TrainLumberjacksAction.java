package territory.game.action.player;

import territory.game.GameColor;
import territory.game.unit.Lumberjack;

import java.io.Serializable;

public class TrainLumberjacksAction extends TrainUnitsAction implements Serializable {
    public TrainLumberjacksAction(GameColor color, int villageIndex, int numUnits) {
        super(color, villageIndex, numUnits);
    }

    @Override
    public Class<Lumberjack> getUnitClass(){
        return Lumberjack.class;
    }
}
