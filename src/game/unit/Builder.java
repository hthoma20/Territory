package game.unit;

import game.Copyable;
import game.RNG;
import game.action.PlaceStoneAction;
import game.action.TickAction;
import game.construction.BuildSlot;
import game.construction.Buildable;
import game.construction.MineSlot;
import game.player.Player;

import java.util.Arrays;
import java.util.List;

public class Builder extends Unit {

    private BuildSlot target;

    //probability to build at each tick, if in range of target
    private static double buildProbability = .05;

    public Builder(Player owner, double x, double y) {
        super(owner, x, y);
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

        return Arrays.asList(new PlaceStoneAction(owner, target.getBuildable(), 5));
    }
}
