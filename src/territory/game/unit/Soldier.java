package territory.game.unit;

import javafx.scene.canvas.GraphicsContext;
import territory.game.RNG;
import territory.game.action.tick.DealDamageAction;
import territory.game.action.tick.TickAction;
import territory.game.player.Player;
import territory.game.target.PatrolArea;
import territory.game.target.PointTarget;
import territory.game.target.Target;
import territory.game.unit.stats.SoldierStats;
import territory.gui.CanvasPainter;
import territory.util.GlobalConstants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Soldier extends Unit implements Serializable {

    private PatrolArea patrolArea;

    private SoldierStats stats;

    public Soldier(Player owner, double x, double y, SoldierStats stats) {
        super(owner.getColor(), x, y);
        this.stats = stats;
    }

    @Override
    public SoldierStats getStats() {
        return stats;
    }

    public static int getGoldPrice(){
        return GlobalConstants.SOLDIER_GOLD;
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
        if(!RNG.withProbability(stats.getAttackProbability())){
            return null;
        }

        return Arrays.asList(new DealDamageAction(this.color, unit, stats.getAttackStrength()));
    }

    @Override
    public void kill(){}

    @Override
    public void paintHighlightOn(GraphicsContext gc){
        super.paintHighlightOn(gc);

        if(patrolArea != null){
            CanvasPainter.strokeCircle(gc, patrolArea.getX(), patrolArea.getY(), patrolArea.getRadius());
        }
    }
}
