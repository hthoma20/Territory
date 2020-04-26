package territory.game.action;

import territory.game.player.Player;

public class GiveStoneAction extends TickAction {
    private int count;

    public GiveStoneAction(Player player, int count) {
        super(player);

        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
