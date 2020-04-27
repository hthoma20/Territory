package territory.game.action.player;

import territory.game.GameColor;
import territory.game.construction.BuildProject;
import territory.game.construction.BuildType;

import java.io.Serializable;

public class DirectBuilderAction extends DirectUnitAction implements Serializable {

    BuildType type;

    public DirectBuilderAction(GameColor color, int unitIndex, int targetIndex, BuildType type) {
        super(color, unitIndex, targetIndex);
        this.type = type;
    }

    public BuildType getType() {
        return type;
    }
}
