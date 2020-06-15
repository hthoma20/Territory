package territory.game.sprite;

import javafx.scene.canvas.GraphicsContext;
import territory.game.GameState;
import territory.game.LocalGame;
import territory.game.Tickable;
import territory.game.action.tick.TickAction;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.List;

public abstract class Sprite implements Tickable, Serializable {
    protected double x, y;

    protected double rotation = 0;

    public Sprite(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Sprite(Sprite src){
        this.x = src.x;
        this.y = src.y;
        this.rotation = src.rotation;
    }

    public abstract boolean containsPoint(double x, double y);

    public abstract void paintOn(GraphicsContext gc);

    public abstract void paintHighlightOn(GraphicsContext gc);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTopX(){
        return x - getWidth()/2;
    }

    public double getTopY(){
        return y - getHeight()/2;
    }

    public abstract double getWidth();
    public abstract double getHeight();

    public double getRotation() {
        return rotation;
    }

    @Override
    public List<TickAction> tick(GameState currentState){
        return null;
    }


    /**
     * @param x the x-coord of the point
     * @param y the y-coord of the point
     * @return the distance from the given point to this sprite
     */
    public abstract double distanceFrom(double x, double y);


    /**
     * @param vector the vector to get the angle of
     * @return the rotation angle represented by the given vector
     */
    public static double rotation(Point2D vector){
        double rotation = vector.angle(1, 0);

        if(vector.getY() < 0){
            rotation = 360 - rotation;
        }

        return rotation;
    }
}
