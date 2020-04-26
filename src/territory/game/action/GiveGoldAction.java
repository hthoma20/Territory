package territory.game.action;

import territory.game.player.Player;

public class GiveGoldAction extends TickAction {
    private int count;

    public GiveGoldAction(Player player, int count) {
        super(player);

        this.count = count;
    }

    public int getCount() {
        return count;
    }
}