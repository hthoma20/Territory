package territory.game.action.tick;

import territory.game.GameColor;
import territory.game.action.GameAction;
import territory.game.player.Player;

public abstract class TickAction extends GameAction {
    public TickAction(GameColor color) {
        super(color);
    }
}
