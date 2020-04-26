package territory.game.action;

import territory.game.player.Player;

public abstract class PlayerAction extends GameAction {
    public PlayerAction(Player player) {
        super(player);
    }
}
