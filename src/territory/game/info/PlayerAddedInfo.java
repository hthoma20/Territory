package territory.game.info;

import java.io.Serializable;
import java.util.List;

public class PlayerAddedInfo extends JoinInfo implements Serializable {

    //list of all players in the game
    private List<String> playerNames;

    public PlayerAddedInfo(List<String> playerNames) {
        super("Added a player.");

        this.playerNames = playerNames;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }
}
