package game.unit;

import game.Copyable;
import game.GameColor;
import game.player.Player;
import game.sprite.ImageSprite;

public abstract class Unit extends ImageSprite implements Copyable<Unit> {

    protected Player owner;

    public Unit(Player owner, double x, double y) {
        super(x, y);
        this.owner = owner;
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
}
