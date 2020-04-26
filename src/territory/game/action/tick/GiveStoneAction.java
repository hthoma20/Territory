package territory.game.action.tick;

import territory.game.GameColor;
import territory.game.player.Player;

public class GiveStoneAction extends TickAction {
    private int count;

    public GiveStoneAction(GameColor color, int count) {
        super(color);

        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
