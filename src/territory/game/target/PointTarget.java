package territory.game.target;

import territory.game.Copyable;

import java.io.Serializable;

public class PointTarget implements Serializable, Copyable<PointTarget>, Target {
    private double x, y;

    public PointTarget(double x, double y){
        this.x = x;
        this.y = y;
    }

    public PointTarget(PointTarget src){
        this.x = src.x;
        this.y = src.y;
    }

    public PointTarget copy(){
        return new PointTarget(this);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }
}
