package territory.game;

import territory.game.action.TickAction;

import java.util.List;

public interface Tickable {
    /**
     * Progress this object's state by one tick
     *
     * @return a queue of actions to be taken at the end of this tick
     *          or null if no actions need to be taken
     */
    List<TickAction> tick();
}
