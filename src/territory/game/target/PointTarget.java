package territory.game.target;

import java.io.Serializable;

public class PointTarget implements Serializable, Target {
    private double x, y;

    public PointTarget(double x, double y){
        this.x = x;
        this.y = y;
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
