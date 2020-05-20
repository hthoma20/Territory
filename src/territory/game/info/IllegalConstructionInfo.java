package territory.game.info;

import java.io.Serializable;

public class IllegalConstructionInfo extends GameInfo implements Serializable {
    public IllegalConstructionInfo() {
        super("Illegal construction placement");
    }
}
