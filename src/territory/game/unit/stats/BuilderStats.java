package territory.game.unit.stats;

import java.io.Serializable;

public class BuilderStats extends UnitStats implements Serializable {

    private double buildProbability;

    public BuilderStats(int health, double range, double speed, double buildProbability){
        super(health, range, speed);
        this.buildProbability = buildProbability;
    }

    public BuilderStats(){
        super();
        this.buildProbability = .05;
        super.speed = .85;
    }

    public BuilderStats(BuilderStats src){
        super(src);
        this.buildProbability = src.buildProbability;
    }

    @Override
    public BuilderStats copy() {
        return new BuilderStats(this);
    }
}
