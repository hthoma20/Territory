package territory.game.action;

import territory.game.player.Player;

public abstract class TickAction extends GameAction {
    public TickAction(Player player) {
        super(player);
    }
}
