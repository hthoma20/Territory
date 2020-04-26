package territory.game.action.player;

import territory.game.GameColor;
import territory.game.player.Player;

import java.io.Serializable;

public class TrainMinersAction extends TrainUnitsAction implements Serializable {
    public TrainMinersAction(GameColor color, int villageIndex, int numUnits) {
        super(color, villageIndex, numUnits);
    }
}
