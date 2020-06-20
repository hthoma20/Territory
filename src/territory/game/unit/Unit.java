package territory.game.unit;

import territory.game.GameColor;
import territory.game.GameState;
import territory.game.Indexable;
import territory.game.action.tick.TickAction;
import territory.game.sprite.ImageSprite;
import territory.game.sprite.Sprite;
import javafx.geometry.Point2D;
import territory.game.target.Target;
import territory.game.unit.stats.UnitStats;

import java.io.Serializable;
import java.util.List;

public abstract class Unit extends ImageSprite
                            implements Indexable, Target, Serializable {

    protected GameColor color;

    private int index = -1;

    public Unit(GameColor color, double x, double y) {
        super(x, y);
        this.color = color;
    }

    //what are we targeting?
    protected abstract Target getTarget();

    public abstract UnitStats getStats();

    //called when we are at the target
    protected abstract List<TickAction> atTarget();

    //called when the unit is killed
    public abstract void kill();

    /**
     * Move toward target, or call atTarget if the target is in range
     * @return a list of TickActions to be taken if the Unit is at its target
     *          or null if they are not
     * @param currentState
     */
    @Override
    public List<TickAction> tick(GameState currentState){
        Target target = this.getTarget();

        if(target == null){
            return null;
        }

        UnitStats stats = getStats();

        Point2D location = new Point2D(x, y);
        Point2D destination = new Point2D(target.getX(), target.getY());

        Point2D distance = destination.subtract(location);

        //if we are at the target
        if(distance.magnitude() <= stats.getRange()){
            return this.atTarget();
        }

        //otherwise we are not at the target, move towards it
        Point2D velocity = distance.normalize().multiply(stats.getSpeed());

        //check if we collide with a something impassible
        if(canMove(currentState, velocity)){
            x += velocity.getX();
            y += velocity.getY();

            this.rotation = Sprite.rotation(velocity);
        }
        //if you can't move, back it up
        else {
            x -= velocity.getX();
            y -= velocity.getY();
        }

        return null;
    }

    private boolean canMove(GameState state, Point2D velocity){

        double newX = x + velocity.getX();
        double newY = y + velocity.getY();

        for(Sprite collidable : state.getAllCollidables(color)){
            if(collidable.containsPoint(newX, newY)){
                return false;
            }
        }

        return true;
    }

    @Override
    public GameColor getColor() {
        return color;
    }

    @Override
    public void setIndex(int index){
        this.index = index;
    }

    @Override
    public int getIndex(){
        return this.index;
    }

    public void dealDamage(int damage){
        getStats().decrementHealth(damage);
    }

    public int getHealth() {
        return getStats().getHealth();
    }
}
