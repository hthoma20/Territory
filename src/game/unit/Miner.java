package game.unit;

import game.Tickable;
import game.construction.Mine;
import game.player.Player;
import javafx.geometry.Point2D;

public class Miner extends Unit {

    private Mine target;

    private static float speed = 2;

    public Miner(Player owner, double x, double y) {
        super(owner, x, y);
    }

    public Miner(Miner src){
        super(src);

        this.target = src.target;
    }

    public void setTarget(Mine target){
        this.target = target;
    }

    public static int getGoldPrice(){
        return 10;
    }

    @Override
    public void tick() {
        if(target == null){
            return;
        }

        Point2D location = new Point2D(x, y);
        Point2D destination = new Point2D(target.getX(), target.getY());

        Point2D velocity = destination.subtract(location).normalize().multiply(speed);

        x += velocity.getX();
        y += velocity.getY();
    }
}
