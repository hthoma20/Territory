package territory.game.info;

import territory.game.unit.Unit;

import java.io.Serializable;

public class IllegalTrainUnitInfo extends GameInfo implements Serializable {
    public IllegalTrainUnitInfo(Class<? extends Unit> unitClass) {
        super(String.format("%ss cannot be trained from this village", unitClass.getSimpleName()));
    }
}
