package territory.game.info;

import java.io.Serializable;

public class PlayerAddedInfo extends JoinInfo implements Serializable {
    public PlayerAddedInfo(String message) {
        super(message);
    }
}
