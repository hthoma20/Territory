package game.construction;

import game.unit.Target;

public interface Buildable extends Target {

    /**
     * @return whether or not the object is done being built
     */
    public boolean isComplete();

    /**
     * Give stone to this buildable
     * @param stone how much stone to give
     * @return the amount of stone taken
     */
    public int giveStone(int stone);
}
