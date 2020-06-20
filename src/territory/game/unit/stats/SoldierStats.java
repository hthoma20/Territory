package territory.game.unit.stats;

import territory.util.GlobalConstants;

import java.io.Serializable;

public class SoldierStats extends UnitStats implements Serializable {

    private double attackProbability;
    private int attackStrength;

    public SoldierStats(Builder builder) {
        super(builder);
        this.attackProbability = builder.attackProbability;
        this.attackStrength = builder.attackStrength;
    }

    public double getAttackProbability() {
        return attackProbability;
    }

    public int getAttackStrength() {
        return attackStrength;
    }

    public static class Builder extends UnitStats.Builder {
        private double attackProbability = GlobalConstants.DEFAULT_ATTACK_PROBABILITY;
        private int attackStrength = GlobalConstants.DEFAULT_ATTACK_STRENGTH;

        public Builder(){
            this.speed = GlobalConstants.DEFAULT_SOLDIER_SPEED;
        }

        public Builder attackProbability(double attackProbability) {
            this.attackProbability = attackProbability;
            return this;
        }

        public Builder attackStrength(int attackStrength){
            this.attackStrength = attackStrength;
            return this;
        }

        @Override
        public SoldierStats build(){
            return new SoldierStats(this);
        }
    }
}
