package game.action;

import game.player.Player;

public class TrainUnitsAction extends PlayerAction {

    private int villageIndex;
    private int numUnits;

    public TrainUnitsAction(Player player, int villageIndex, int numUnits) {
        super(player);

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
