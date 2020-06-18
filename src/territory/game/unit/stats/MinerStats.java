package territory.game.unit.stats;

import java.io.Serializable;

public class MinerStats extends UnitStats implements Serializable {

    private double mineProbability;

    public MinerStats(int health, double range, double speed, double mineProbability){
        super(health, range, speed);
        this.mineProbability = mineProbability;
    }

    public MinerStats(){
        super();
        this.mineProbability = .05;
        super.speed = 1;
    }
}
