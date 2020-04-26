package territory.game.unit;

import territory.game.RNG;
import territory.game.action.tick.PlaceStoneAction;
import territory.game.action.tick.TickAction;
import territory.game.construction.BuildProject;
import territory.game.construction.BuildSlot;
import territory.game.player.Player;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Builder extends Unit implements Serializable {

    private BuildSlot target;

    private BuildProject project;

    //probability to build at each tick, if in range of target
    private static double buildProbability = .05;

    public Builder(Player owner, double x, double y) {
        super(owner.getColor(), x, y);
    }

    public Builder(Builder src) {
        super(src);

        this.target = src.target;
    }

    @Override
    public Builder copy(){
        return new Builder(this);
    }

    public static int getGoldPrice(){
        return 10;
    }

    public void setTarget(BuildSlot target){
        this.target = target;
        target.incUnitCount();
    }

    public void setProject(BuildProject project){
        this.project = project;

        setTarget(project.getOpenBuildSlot());
    }

    @Override
    public List<TickAction> tick(){
        findTarget();

        return super.tick();
    }

    private void findTarget(){
        //should we switch targets?
        BuildSlot newTarget = target.getBuildable().getOpenBuildSlot();

        if(newTarget.getUnitCount()+1 < target.getUnitCount()){ //plus one to account for this miner
            target.decUnitCount();
            newTarget.incUnitCount();

            target = newTarget;
        }
    }

    @Override
    protected Target getTarget(){
        return this.target;
    }

    @Override
    protected List<TickAction> atTarget() {
        if(!RNG.withProbability(buildProbability)){
            return null;
        }

        return Arrays.asList(new PlaceStoneAction(color, target.getBuildable(), 5));
    }
}
