package territory.game.construction;

import javafx.geometry.Point2D;

public interface Construction {
    /**
     * @return how close to this construction can something can be built
     */
    double getBuildZoneRadius();

    double getX();
    double getY();
}
