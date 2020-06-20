package territory.game.unit.stats;

import territory.util.GlobalConstants;

import java.io.Serializable;

public class BuilderStats extends UnitStats implements Serializable {

    private double buildProbability;

    public BuilderStats(Builder builder){
        super(builder);
        this.buildProbability = builder.buildProbability;
    }

    public double getBuildProbability() {
        return buildProbability;
    }

    public static class Builder extends UnitStats.Builder {
        private double buildProbability = GlobalConstants.DEFAULT_BUILD_PROBABILITY;

        public Builder(){
            this.speed = GlobalConstants.DEFAULT_BUILDER_SPEED;
        }

        public Builder buildProbability(double buildProbability){
            this.buildProbability = buildProbability;
            return this;
        }

        @Override
        public BuilderStats build(){
            return new BuilderStats(this);
        }
    }

}
