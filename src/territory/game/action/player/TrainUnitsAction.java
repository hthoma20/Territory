package territory.game.action.player;

import territory.game.GameColor;
import territory.game.player.Player;
import territory.game.unit.Unit;

import java.io.Serializable;

public abstract class TrainUnitsAction extends PlayerAction implements Serializable {

    private int villageIndex;
    private int numUnits;

    public TrainUnitsAction(GameColor color, int villageIndex, int numUnits) {
        super(color);

        this.villageIndex = villageIndex;
        this.numUnits = numUnits;
    }

    public int getVillageIndex() {
        return villageIndex;
    }

    public int getNumUnits() {
        return numUnits;
    }

    public abstract Class<? extends Unit> getUnitClass();
}
