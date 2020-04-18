package game.action;

import game.player.Player;

public abstract class PlayerAction extends GameAction {
    public PlayerAction(Player player) {
        super(player);
    }
}
