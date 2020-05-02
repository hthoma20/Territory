package territory.game.unit;

import territory.game.RNG;
import territory.game.action.tick.DealDamageAction;
import territory.game.action.tick.PlaceStoneAction;
import territory.game.action.tick.TickAction;
import territory.game.construction.BuildProject;
import territory.game.player.Player;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Soldier extends Unit implements Serializable {

    private Unit target;

    //probability to attack at each tick, if in range of target
    private static double attackProbability = .05;

    //damage done per attack
    private static int attackStrength = 2;

    public Soldier(Player owner, double x, double y) {
        super(owner.getColor(), x, y);
    }

    public Soldier(Soldier src) {
        super(src);

        this.target = src.target;
    }

    @Override
    public Soldier copy(){
        return new Soldier(this);
    }

    public static int getGoldPrice(){
        return 10;
    }

    @Override
    protected Target getTarget(){
        return this.target;
    }

    public void setTarget(Unit target){
        if(target.color == this.color){
            throw new RuntimeException("Soldier targeting the same color");
        }

        this.target = target;
    }

    @Override
    protected List<TickAction> atTarget() {
        if(!RNG.withProbability(attackProbability)){
            return null;
        }

        return Arrays.asList(new DealDamageAction(this.color, target, attackStrength));
    }

    @Override
    public void kill(){}
}
