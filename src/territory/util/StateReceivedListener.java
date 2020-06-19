package territory.util;

import territory.game.GameState;

public interface StateReceivedListener {

    public static StateReceivedListener DO_NOTHING = () -> {};

    void run();

    /**
     * Compose these two listeners, so that this one executes, and then the next one does
     * @param after the procedure to execute after this one
     * @return a new listener which runs this procedure, then the "after" procedure
     */
    default StateReceivedListener andThen(StateReceivedListener after){
        return () -> {
            this.run();
            after.run();
        };
    }
}
