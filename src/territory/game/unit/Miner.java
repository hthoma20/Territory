package territory.game.unit;

import territory.game.RNG;
import territory.game.action.GiveGoldAction;
import territory.game.action.GiveStoneAction;
import territory.game.action.TickAction;
import territory.game.construction.Mine;
import territory.game.construction.MineSlot;
import territory.game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Miner extends Unit {

    private MineSlot target;

    //probability to mine at each tick, if in range of target
    private static double mineProbability = .05;

    public Miner(Player owner, double x, double y) {
        super(owner, x, y);
    }

    @Override
    protected Target getTarget() {
        return target;
    }

    public Miner(Miner src){
        super(src);

        this.target = src.target;
    }

    @Override
    public Miner copy(){
        return new Miner(this);
    }

    public void setTarget(MineSlot target){
        this.target = target;
        target.incUnitCount();
    }

    public static int getGoldPrice(){
        return 10;
    }

    @Override
    public List<TickAction> tick() {
        findTarget();

        return super.tick();
    }

    private void findTarget(){
        //should we switch targets?
        MineSlot newTarget = target.getMine().getOpenMineSlot();

        if(newTarget.getUnitCount()+1 < target.getUnitCount()){ //plus one to account for this miner
            target.decUnitCount();
            newTarget.incUnitCount();

            target = newTarget;
        }
    }

    @Override
    protected List<TickAction> atTarget(){
        //choose whether to mine (actually whether not to mine)
        if(!RNG.withProbability(mineProbability)){
            return null;
        }

        //we have chosen to mine
        List<TickAction> actions = new ArrayList<>();

        Mine mine = target.getMine();
        int gold = mine.getGold();
        int stone = mine.getStone();

        if(gold > 0){
            actions.add(new GiveGoldAction(owner, gold));
        }
        if(stone > 0){
            actions.add(new GiveStoneAction(owner, stone));
        }

        if(actions.size() > 0){
            return actions;
        }
        return null;
    }
}