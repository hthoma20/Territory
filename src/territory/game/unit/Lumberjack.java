package territory.game.unit;

import territory.game.GameState;
import territory.game.RNG;
import territory.game.action.tick.FellTreeAction;
import territory.game.action.tick.GiveGoldAction;
import territory.game.action.tick.GiveStoneAction;
import territory.game.action.tick.TickAction;
import territory.game.construction.Mine;
import territory.game.construction.Tree;
import territory.game.player.Player;
import territory.game.target.MineSlot;
import territory.game.target.Target;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Lumberjack extends Unit implements Serializable {

    private Tree target;

    //probability to mine at each tick, if in range of target
    private static double chopProbability = .05;
    private static int chopStrength = 2;

    public Lumberjack(Player owner, double x, double y) {
        super(owner.getColor(), x, y);

        super.speed = 1;
    }

    @Override
    protected Target getTarget() {
        return target;
    }

    public Lumberjack(Lumberjack src){
        super(src);

        this.target = src.target;
    }

    @Override
    public Lumberjack copy(){
        return new Lumberjack(this);
    }

    public void setTarget(Tree target){
        this.target = target;
    }

    public static int getGoldPrice(){
        return 5;
    }

    @Override
    public List<TickAction> tick(GameState currentState) {
        //if we should find a new target
        if(target != null && !target.isAlive()) {
            findTarget(currentState);
        }

        return super.tick(currentState);
    }

    /**
     * Set the target to be the nearest alive tree
     */
    private void findTarget(GameState state){
        //just look at alive trees
        List<Tree> trees = state.getTrees().stream().filter(Tree::isAlive).collect(Collectors.toList());

        if(trees.isEmpty()){
            target = null;
            return;
        }

        Tree nearestTree = Collections.min(trees, Comparator.comparingDouble(tree ->
                this.distanceFrom(tree.getX(), tree.getY())));

        this.target = nearestTree;
    }

    @Override
    protected List<TickAction> atTarget(){
        if( !target.isAlive() ){
            System.out.println("Lumberjack has a dead target");
            return null;
        }

        if( !RNG.withProbability(chopProbability) ){
            return null;
        }

        target.chop(chopStrength);

        //if we chopped down the tree
        if( !target.isAlive() ){
            return Arrays.asList(new FellTreeAction(color, target));
        }

        return null;
    }

    @Override
    public void kill(){
        //do nothing
    }
}
