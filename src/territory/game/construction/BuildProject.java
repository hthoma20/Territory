package territory.game.construction;

import territory.game.Indexable;

public interface BuildProject extends Indexable {
    boolean isComplete();

    BuildSlot getOpenBuildSlot();
}
