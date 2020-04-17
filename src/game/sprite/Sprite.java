package game.sprite;

import game.LocalGame;
import javafx.scene.image.Image;

public abstract class Sprite {

    public final int id = LocalGame.getUniqueId();

    protected double x, y;

    public Sprite(double x, double y){
        this.x = x;
        this.y = y;
    }

    public abstract boolean containsPoint(double x, double y);

    public abstract Image getImage();

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
