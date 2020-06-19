package territory.game.info;

import java.io.Serializable;

public class IllegalUpgradeInfo extends IllegalActionInfo implements Serializable {
    public IllegalUpgradeInfo(String message) {
        super(message);
    }
}
