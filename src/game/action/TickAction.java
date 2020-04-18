package game.action;

import game.player.Player;

public abstract class TickAction extends GameAction {
    public TickAction(Player player) {
        super(player);
    }
}
