package territory.game.action.player;

import territory.game.GameColor;
import territory.game.target.PatrolArea;

import java.io.Serializable;
import java.util.Set;

public class DirectSoldiersAction extends PlayerAction implements Serializable {

    Set<Integer> indices;
    PatrolArea patrolArea;

    public DirectSoldiersAction(GameColor color, Set<Integer> indices, PatrolArea patrolArea) {
        super(color);

        this.indices = indices;
        this.patrolArea = patrolArea;
    }

    public Set<Integer> getIndices() {
        return indices;
    }

    public PatrolArea getPatrolArea() {
        return patrolArea;
    }
}
