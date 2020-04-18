package game.construction;

import game.Copyable;
import game.unit.Target;

/**
 * Represents a location that a Miner can mine from
 */
public class MineSlot implements Copyable<MineSlot>, Target {
    private Mine mine;
    private double x, y;

    private int minerCount = 0;

    public MineSlot(Mine mine, double x, double y){
        this.mine = mine;
        this.x = x;
        this.y = y;
    }

    public MineSlot(MineSlot src){
        this.mine = src.mine;

        this.x = src.x;
        this.y = src.y;

        this.minerCount = src.minerCount;
    }

    @Override
    public MineSlot copy(){
        return new MineSlot(this);
    }

    public Mine getMine() {
        return mine;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public int getMinerCount(){
        return minerCount;
    }

    public void incMinerCount(){
        minerCount++;
    }

    public void decMinerCount(){
        minerCount--;
    }
}
