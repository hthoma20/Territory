package territory.game;

import territory.game.action.tick.TickAction;

import java.util.List;

public interface Tickable {
    /**
     * Progress this object's state by one tick
     *
     * @return a queue of actions to be taken at the end of this tick
     *          or null if no actions need to be taken
     * @param currentState the state of the game
     */
    List<TickAction> tick(GameState currentState);
}
