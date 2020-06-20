package territory.game.unit.stats;

import territory.util.GlobalConstants;

import java.io.Serializable;

/**
 * Statistics for units
 */
public abstract class UnitStats implements Serializable {

    protected int health;
    protected double range;
    protected double speed;

    protected UnitStats(Builder builder) {
        this.health = builder.health;
        this.range = builder.range;
        this.speed = builder.speed;
    }

    /**
     * Take away the given number of health points
     * @param damage the damage to deal
     */
    public void decrementHealth(int damage){
        health -= damage;
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


    public abstract static class Builder {
        protected int health = GlobalConstants.DEFAULT_HEALTH;
        protected double range = GlobalConstants.DEFAULT_RANGE;
        protected double speed = GlobalConstants.DEFAULT_SPEED;

        public Builder health(int health) {
            this.health = health;
            return this;
        }

        public Builder range(double range) {
            this.range = range;
            return this;
        }

        public Builder speed(double speed) {
            this.speed = speed;
            return this;
        }

        public abstract UnitStats build();
    }

}
