package territory.game.unit.stats;

import territory.util.GlobalConstants;

import java.io.Serializable;

public class MinerStats extends UnitStats implements Serializable {

    private double mineProbability;

    public MinerStats(Builder builder) {
        super(builder);
        this.mineProbability = builder.mineProbability;
    }

    public double getMineProbability() {
        return mineProbability;
    }

    public static class Builder extends UnitStats.Builder {
        private double mineProbability = GlobalConstants.DEFAULT_MINE_PROBABILITY;

        public Builder(){
            this.speed = GlobalConstants.DEFAULT_MINER_SPEED;
        }

        public Builder mineProbability(double mineProbability) {
            this.mineProbability = mineProbability;
            return this;
        }

        @Override
        public MinerStats build(){
            return new MinerStats(this);
        }
    }
}
