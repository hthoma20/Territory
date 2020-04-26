package territory.game.info;

import java.io.Serializable;

public class IllegalActionInfo extends GameInfo implements Serializable {
    public IllegalActionInfo(String message) {
        super(message);
    }
}
