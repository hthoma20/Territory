package territory.game.target;

import java.io.Serializable;

public abstract class Slot implements Target, Serializable {
    private double x, y;

    private int unitCount = 0;

    public Slot(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    public int getUnitCount(){
        return unitCount;
    }

    public void incUnitCount(){
        unitCount++;
    }

    public void decUnitCount(){
        unitCount--;
    }

    @Override
    public String toString(){
        return "Units: " + unitCount;
    }
}
