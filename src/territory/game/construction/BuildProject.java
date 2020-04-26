package territory.game.construction;

import territory.game.Indexable;

import java.io.Serializable;

public interface BuildProject extends Indexable {
    boolean isComplete();

    BuildSlot getOpenBuildSlot();
}
