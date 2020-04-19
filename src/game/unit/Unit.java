package game.unit;

import game.Copyable;
import game.GameColor;
import game.Indexable;
import game.action.TickAction;
import game.player.Player;
import game.sprite.ImageSprite;
import game.sprite.Sprite;
import javafx.geometry.Point2D;

import java.util.List;

public abstract class Unit extends ImageSprite implements Copyable<Unit>, Indexable {

    protected Player owner;

    private int index = -1;

    //movement per tick
    protected float speed = 2;

    //distance from target to take action
    protected float range = 2;

    public Unit(Player owner, double x, double y) {
        super(x, y);
        this.owner = owner;
    }

    public Unit(Unit src){
        super(src);

        this.owner = src.owner;
        this.index = src.index;
        this.speed = src.speed;
        this.range = src.range;
    }

    //what are we targeting?
    protected abstract Target getTarget();

    //called when we are at the target
    protected abstract List<TickAction> atTarget();

    /**
     * Move toward target, or call atTarget if the target is in range
     * @return a list of TickActions to be taken if the Unit is at its target
     *          or null if they are not
     */
    @Override
    public List<TickAction> tick(){
        Target target = this.getTarget();

        if(target == null){
            return null;
        }

        Point2D location = new Point2D(x, y);
        Point2D destination = new Point2D(target.getX(), target.getY());

        Point2D distance = destination.subtract(location);

        //if we are at the target
        if(distance.magnitude() <= range){
            return this.atTarget();
        }

        //otherwise we are not at the target, move towards it
        Point2D velocity = distance.normalize().multiply(speed);

        x += velocity.getX();
        y += velocity.getY();

        this.rotation = velocity.angle(1, 0);

        if(velocity.getY() < 0){
            this.rotation = 360 - this.rotation;
        }

        return null;
    }

    @Override
    public GameColor getColor() {
        return owner.getColor();
    }

    @Override
    public void setIndex(int index){
        this.index = index;
    }

    @Override
    public int getIndex(){
        return this.index;
    }
}
