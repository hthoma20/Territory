package game.unit;

import game.Copyable;
import game.action.TickAction;
import game.construction.BuildSlot;
import game.construction.Buildable;
import game.construction.MineSlot;
import game.player.Player;

import java.util.List;

public class Builder extends Unit {

    private BuildSlot target;

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
        return null;
    }
}
