package territory.game;

import territory.game.action.player.PlayerAction;

public interface Game {
    void receiveAction(PlayerAction action);

    void start();
}
