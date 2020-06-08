package territory.game.unit;

import territory.game.GameState;
import territory.game.RNG;
import territory.game.action.tick.GiveGoldAction;
import territory.game.action.tick.GiveStoneAction;
import territory.game.action.tick.TickAction;
import territory.game.construction.Mine;
import territory.game.target.MineSlot;
import territory.game.player.Player;
import territory.game.target.Target;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Miner extends Unit implements Serializable {

    private MineSlot target;

    //probability to mine at each tick, if in range of target
    private static double mineProbability = .05;

    public Miner(Player owner, double x, double y) {
        super(owner.getColor(), x, y);

        super.speed = 1;
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
        if(this.target != null){
            this.target.decUnitCount();
        }

        this.target = target;
        target.incUnitCount();
    }

    public static int getGoldPrice(){
        return 5;
    }

    @Override
    public List<TickAction> tick(GameState currentState) {
        if(target != null) {
            findTarget();
        }

        return super.tick(currentState);
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
            actions.add(new GiveGoldAction(color, gold));
        }
        if(stone > 0){
            actions.add(new GiveStoneAction(color, stone));
        }

        if(actions.size() > 0){
            return actions;
        }
        return null;
    }

    @Override
    public void kill(){
        if(target == null){
            return;
        }

        target.decUnitCount();
    }
}
