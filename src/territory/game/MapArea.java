package territory.game;

import java.io.Serializable;

/**
 * Defines an area of the map
 */
public interface MapArea extends Serializable {

    /**
     * @param x the x-coord of the point to test
     * @param y the y-coord of the point to test
     * @return whether the given point is within this area
     */
    boolean contains(double x, double y);
}
