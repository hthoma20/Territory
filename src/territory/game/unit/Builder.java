package territory.game.unit;

import territory.game.RNG;
import territory.game.action.tick.PlaceStoneAction;
import territory.game.action.tick.TickAction;
import territory.game.target.BuildProject;
import territory.game.player.Player;
import territory.game.target.Target;
import territory.game.unit.stats.BuilderStats;
import territory.util.GlobalConstants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Builder extends Unit implements Serializable {

    private BuildProject project;

    //probability to build at each tick, if in range of target
    private BuilderStats stats;

    public Builder(Player owner, double x, double y, BuilderStats stats) {
        super(owner.getColor(), x, y);
        this.stats = stats;
    }

    @Override
    public BuilderStats getStats(){
        return this.stats;
    }

    public static int getGoldPrice(){
        return GlobalConstants.BUILDER_GOLD;
    }

    public void setProject(BuildProject project){
        if(this.project != null){
            this.project.removeBuilder(this);
        }

        this.project = project;
        project.addBuilder(this);
    }

    @Override
    protected Target getTarget(){
        if(project == null){
            return null;
        }
        return project.getBuildSlot(this);
    }

    @Override
    protected List<TickAction> atTarget() {
        if(!RNG.withProbability(stats.getBuildProbability())){
            return null;
        }

        return Arrays.asList(new PlaceStoneAction(color, project.getBuildable(this), 5));
    }

    @Override
    public void kill(){
        if(this.project != null) {
            this.project.removeBuilder(this);
        }
    }
}
