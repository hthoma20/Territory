package game.construction;

import game.Copyable;
import game.GameColor;
import game.player.Player;
import game.sprite.ImageSprite;

public class Village extends ImageSprite implements Copyable<Village> {

    private Player owner;

    public Village(Player owner, double x, double y){
        super(x, y);
        this.owner = owner;
    }

    public Village(Village src){
        super(src.x, src.y);
        this.owner = src.owner;
    }

    @Override
    public Village copy(){
        return new Village(this);
    }

    @Override
    public GameColor getColor() {
        return owner.getColor();
    }

    public static int getGoldPrice(){
        return 10;
    }
}
