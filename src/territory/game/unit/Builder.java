package territory.game.unit;

import territory.game.RNG;
import territory.game.action.tick.PlaceStoneAction;
import territory.game.action.tick.TickAction;
import territory.game.target.BuildProject;
import territory.game.player.Player;
import territory.game.target.Target;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Builder extends Unit implements Serializable {

    private BuildProject project;

    //probability to build at each tick, if in range of target
    private static double buildProbability = .05;

    public Builder(Player owner, double x, double y) {
        super(owner.getColor(), x, y);
    }

    public Builder(Builder src) {
        super(src);

        if(src.project == null){
            this.project = null;
        }
        else{
            this.project = src.project.copy();
        }
    }

    @Override
    public Builder copy(){
        return new Builder(this);
    }

    public static int getGoldPrice(){
        return 10;
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
        if(!RNG.withProbability(buildProbability)){
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
