package territory.game.action.player;

import territory.game.Game;
import territory.game.GameColor;
import territory.game.action.GameAction;
import territory.game.player.Player;

import java.io.Serializable;

public abstract class PlayerAction extends GameAction implements Serializable {

    public PlayerAction(GameColor color){
        super(color);
    }

}
