package territory.game.action;

import territory.game.player.Player;

public abstract class GameAction {
    //the player who is taking the action
    private Player player;

    public GameAction(Player player){
        this.player = player;
    }

    public Player getPlayer(){
        return player;
    }
}
