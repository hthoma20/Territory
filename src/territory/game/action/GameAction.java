package territory.game.action;

import territory.game.GameColor;
import territory.game.player.Player;

import java.io.Serializable;

public abstract class GameAction implements Serializable {
    //the color of the player who is taking the action
    private GameColor color;

    public GameAction(GameColor color){
        this.color = color;
    }

    public GameColor getColor(){
        return color;
    }
}
