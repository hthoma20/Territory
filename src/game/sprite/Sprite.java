package game.sprite;

import game.LocalGame;
import game.Tickable;
import game.action.TickAction;
import javafx.scene.image.Image;

import java.util.List;

public abstract class Sprite implements Tickable {

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

    @Override
    public List<TickAction> tick(){
        return null;
    }
}
