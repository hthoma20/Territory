package territory.game.info;

import java.io.Serializable;

public class GameStartedInfo extends JoinInfo implements Serializable {
    public GameStartedInfo(String message) {
        super(message);
    }
}
