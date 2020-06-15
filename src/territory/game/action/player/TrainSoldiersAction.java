package territory.game.action.player;

import territory.game.GameColor;
import territory.game.unit.Soldier;

import java.io.Serializable;

public class TrainSoldiersAction extends TrainUnitsAction implements Serializable {
    public TrainSoldiersAction(GameColor color, int villageIndex, int numUnits) {
        super(color, villageIndex, numUnits);
    }

    @Override
    public Class<Soldier> getUnitClass(){
        return Soldier.class;
    }
}
