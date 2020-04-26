package territory.game.action.player;

import territory.game.GameColor;
import territory.game.construction.BuildProject;
import territory.game.player.Player;

import java.io.Serializable;

public class DirectBuilderAction extends DirectUnitAction implements Serializable {

    Class<? extends BuildProject> type;

    public DirectBuilderAction(GameColor color, int unitIndex, int targetIndex, Class<? extends BuildProject> type) {
        super(color, unitIndex, targetIndex);
        this.type = type;
    }

    public DirectBuilderAction(GameColor color, int unitIndex, BuildProject project) {
        this(color, unitIndex, project.getIndex(), project.getClass());
    }

    public Class<? extends BuildProject> getType() {
        return type;
    }
}
