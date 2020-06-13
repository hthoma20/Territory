package territory.game.action.tick;

import territory.game.GameColor;
import territory.game.construction.Buildable;
import territory.game.construction.Tree;

public class FellTreeAction extends TickAction {
    private Tree felledTree;

    public FellTreeAction(GameColor color, Tree felledTree) {
        super(color);
        this.felledTree = felledTree;
    }

    public Tree getFelledTree(){
        return felledTree;
    }
}
