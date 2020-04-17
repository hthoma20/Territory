package game.sprite;

import game.LocalGame;
import javafx.scene.image.Image;

public abstract class Sprite {

    private int id;

    protected double x, y;

    public Sprite(double x, double y){
        this.id = LocalGame.getUniqueId();
        this.x = x;
        this.y = y;
    }

    public Sprite(Sprite src){
        this.id = src.id;
        this.x = src.x;
        this.y = src.y;
    }

    public abstract boolean containsPoint(double x, double y);

    public abstract Image getImage();

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getId() {
        return id;
    }
}
