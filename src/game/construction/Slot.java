package game.construction;

import game.Copyable;
import game.unit.Target;

public abstract class Slot implements Copyable<Slot>, Target {
    private double x, y;

    private int unitCount = 0;

    public Slot(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Slot(Slot src){
        this.x = src.x;
        this.y = src.y;
        this.unitCount = src.unitCount;
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
