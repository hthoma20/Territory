package territory.game.action.tick;

import territory.game.GameColor;

public class GiveGoldAction extends TickAction {
    private int count;

    public GiveGoldAction(GameColor color, int count) {
        super(color);

        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
