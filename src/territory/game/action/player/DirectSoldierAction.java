package territory.game.action.player;

import territory.game.GameColor;
import territory.game.target.PatrolArea;

import java.io.Serializable;

public class DirectSoldierAction extends DirectUnitAction implements Serializable {

    private PatrolArea patrolArea;

    public DirectSoldierAction(GameColor color, int unitIndex, PatrolArea patrolArea) {
        super(color, unitIndex, -1);

        this.patrolArea = patrolArea;
    }

    public PatrolArea getPatrolArea() {
        return patrolArea;
    }
}
