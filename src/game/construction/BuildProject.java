package game.construction;

import game.Indexable;

public interface BuildProject extends Indexable {
    boolean isComplete();

    BuildSlot getOpenBuildSlot();
}
