package territory.game.unit.stats;

import territory.game.Copyable;

import java.io.Serializable;

/**
 * Statistics for units
 */
public abstract class UnitStats implements Copyable<UnitStats>, Serializable {

    protected int health;
    protected double range;
    protected double speed;

    public UnitStats(int health, double range, double speed) {
        this.health = health;
        this.range = range;
        this.speed = speed;
    }

    /**
     * Construct stats with default values
     */
    public UnitStats(){
        this.health = 10;
        this.range = 2;
        this.speed = 1;
    }

    public UnitStats(UnitStats src){
        this.health = src.health;
        this.speed = src.speed;
        this.range = src.range;
    }

    public int getHealth() {
        return health;
    }

    public double getRange() {
        return range;
    }

    public double getSpeed() {
        return speed;
    }
}
