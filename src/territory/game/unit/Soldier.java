package territory.game.unit;

import territory.game.RNG;
import territory.game.action.tick.DealDamageAction;
import territory.game.action.tick.TickAction;
import territory.game.player.Player;
import territory.game.target.PatrolArea;
import territory.game.target.PointTarget;
import territory.game.target.Target;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Soldier extends Unit implements Serializable {

    private PatrolArea patrolArea;

    //probability to attack at each tick, if in range of target
    private static double attackProbability = .05;

    //damage done per attack
    private static int attackStrength = 2;

    public Soldier(Player owner, double x, double y) {
        super(owner.getColor(), x, y);

        super.speed = .75;
    }

    public Soldier(Soldier src) {
        super(src);

        if(src.patrolArea == null){
            this.patrolArea = null;
        }
        else{
            this.patrolArea = src.patrolArea.copy();
        }
    }

    @Override
    public Soldier copy(){
        return new Soldier(this);
    }

    public static int getGoldPrice(){
        return 20;
    }

    @Override
    protected Target getTarget(){
        if(patrolArea == null){
            return null;
        }

        return patrolArea.getTarget(this);
    }

    public PatrolArea getPatrolArea() {
        return patrolArea;
    }

    public void setPatrolArea(PatrolArea patrolArea){
        this.patrolArea = patrolArea;
        this.patrolArea.addSoldier(this);
    }

    @Override
    protected List<TickAction> atTarget() {
        Target target = patrolArea.getTarget(this);

        //if we are moving randomly
        if(target instanceof PointTarget){
            patrolArea.reachedTarget(this);
        }

        //if we are attacking
        else if(target instanceof Unit){
            return attackUnit((Unit) target);
        }

        else{
            throw new RuntimeException("Unknown target type for soldier");
        }

        return null;
    }

    /**
     * With probability, deal damage to the given unit
     * @param unit the unit to attack
     * @return a list of actions, representing the damage to do, or null if no damage should be done
     */
    private List<TickAction> attackUnit(Unit unit){
        if(!RNG.withProbability(attackProbability)){
            return null;
        }

        return Arrays.asList(new DealDamageAction(this.color, unit, attackStrength));
    }

    @Override
    public void kill(){}
}
