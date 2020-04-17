package game.construction;

import game.Copyable;
import game.GameColor;
import game.Indexable;
import game.sprite.ImageSprite;

public class Mine extends ImageSprite implements Copyable<Mine>, Indexable {

    private int index = -1;

    public Mine(double x, double y){
        super(x, y);
    }

    public Mine(Mine src){
        super(src);

        this.index = src.index;
    }

    @Override
    public Mine copy() {
        return new Mine(this);
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public GameColor getColor() {
        return null;
    }
}
