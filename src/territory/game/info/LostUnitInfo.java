package territory.game.info;

import territory.game.GameColor;

import java.io.Serializable;

public class LostUnitInfo extends GameInfo implements Serializable {
    private int unitIndex;

    public LostUnitInfo(int unitIndex){
        super(String.format("Player lost unit %s", unitIndex));

        this.unitIndex = unitIndex;
    }

    public int getUnitIndex() {
        return unitIndex;
    }
}
