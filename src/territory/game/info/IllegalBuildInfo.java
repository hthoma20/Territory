package territory.game.info;

import java.io.Serializable;

public class IllegalBuildInfo extends IllegalActionInfo implements Serializable {
    public IllegalBuildInfo(String message) {
        super(message);
    }
}
