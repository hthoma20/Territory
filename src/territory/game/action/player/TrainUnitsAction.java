package territory.game.action.player;

import territory.game.GameColor;
import territory.game.player.Player;

import java.io.Serializable;

public class TrainUnitsAction extends PlayerAction implements Serializable {

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
}
