package territory.game.action;

import territory.game.player.Player;

public class TrainBuildersAction extends TrainUnitsAction {
    public TrainBuildersAction(Player player, int villageIndex, int numUnits) {
        super(player, villageIndex, numUnits);
    }
}
