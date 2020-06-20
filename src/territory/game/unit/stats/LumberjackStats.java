package territory.game.unit.stats;

import territory.util.GlobalConstants;

import java.io.Serializable;

public class LumberjackStats extends UnitStats implements Serializable {

    private double chopProbability;
    private int chopStrength;

    public LumberjackStats(Builder builder) {
        super(builder);
        this.chopProbability = builder.chopProbability;
        this.chopStrength = builder.chopStrength;
    }


    public double getChopProbability() {
        return chopProbability;
    }

    public int getChopStrength() {
        return chopStrength;
    }

    public static class Builder extends UnitStats.Builder {
        private double chopProbability = GlobalConstants.DEFAULT_CHOP_PROBABILITY;
        private int chopStrength = GlobalConstants.DEFAULT_CHOP_STRENGTH;

        public Builder(){
            this.speed = GlobalConstants.DEFAULT_LUMBERJACK_SPEED;
        }

        public Builder chopProbability(double chopProbability) {
            this.chopProbability = chopProbability;
            return this;
        }

        public Builder chopStrength(int chopStrength){
            this.chopStrength = chopStrength;
            return this;
        }

        @Override
        public LumberjackStats build(){
            return new LumberjackStats(this);
        }
    }
}
