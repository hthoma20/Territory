package game.action;

import game.construction.BuildProject;
import game.player.Player;

public class DirectBuilderAction extends DirectUnitAction {

    Class<? extends BuildProject> type;

    public DirectBuilderAction(Player player, int unitIndex, int targetIndex, Class<? extends BuildProject> type) {
        super(player, unitIndex, targetIndex);
        this.type = type;
    }

    public DirectBuilderAction(Player player, int unitIndex, BuildProject project) {
        this(player, unitIndex, project.getIndex(), project.getClass());
    }

    public Class<? extends BuildProject> getType() {
        return type;
    }
}
