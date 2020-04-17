package game.unit;

import game.GameColor;
import game.player.Player;
import game.sprite.ImageSprite;

public abstract class Unit extends ImageSprite {

    protected Player owner;

    public Unit(Player owner, double x, double y) {
        super(x, y);
        this.owner = owner;
    }

    @Override
    public GameColor getColor() {
        return owner.getColor();
    }
}
