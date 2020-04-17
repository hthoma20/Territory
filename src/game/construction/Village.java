package game.construction;

import game.GameColor;
import game.player.Player;
import game.sprite.ImageSprite;

public class Village extends ImageSprite {

    private Player owner;

    public Village(Player owner, double x, double y){
        super(x, y);
        this.owner = owner;
    }

    @Override
    public GameColor getColor() {
        return owner.getColor();
    }

    public static int getGoldPrice(){
        return 10;
    }
}
