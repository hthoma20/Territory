package territory.game;

import territory.game.action.PlayerAction;

public interface Game {
    void receiveAction(PlayerAction action);

    void start();
}
