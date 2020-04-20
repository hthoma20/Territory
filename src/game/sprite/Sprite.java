package game.sprite;

import game.LocalGame;
import game.Tickable;
import game.action.TickAction;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.List;

public abstract class Sprite implements Tickable {

    private int id;

    protected double x, y;

    protected double rotation = 0;

    public Sprite(double x, double y){
        this.id = LocalGame.getUniqueId();
        this.x = x;
        this.y = y;
    }

    public Sprite(Sprite src){
        this.id = src.id;
        this.x = src.x;
        this.y = src.y;
        this.rotation = src.rotation;
    }

    public abstract boolean containsPoint(double x, double y);

    public abstract Image getImage();

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRotation() {
        return rotation;
    }

    public int getId() {
        return id;
    }

    @Override
    public List<TickAction> tick(){
        return null;
    }

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
