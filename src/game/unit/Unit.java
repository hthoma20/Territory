package game.unit;

import game.Copyable;
import game.GameColor;
import game.Indexable;
import game.player.Player;
import game.sprite.ImageSprite;

public abstract class Unit extends ImageSprite implements Copyable<Unit>, Indexable {

    protected Player owner;

    private int index = -1;

    public Unit(Player owner, double x, double y) {
        super(x, y);
        this.owner = owner;
    }

    public Unit(Unit src){
        super(src);

        this.owner = src.owner;
        this.index = src.index;
    }

    @Override
    public GameColor getColor() {
        return owner.getColor();
    }

    @Override
    public Unit copy(){
        if(this.getClass() == Miner.class){
            return new Miner((Miner)this);
        }

        throw new RuntimeException(String.format("Unknown unit type %s", this.getClass().getName()));
    }

    @Override
    public void setIndex(int index){
        this.index = index;
    }

    @Override
    public int getIndex(){
        return this.index;
    }
}
