package game.construction;

import game.Copyable;
import game.GameColor;
import game.Indexable;
import game.player.Player;
import game.sprite.ImageSprite;

public class Post extends Buildable implements Indexable, BuildProject {

    private int index = -1;

    public Post(Player owner, double x, double y){
        super(owner, x, y);
    }

    public Post(Post src){
        super(src);

        this.index = src.index;
    }

    @Override
    public Post copy(){
        return new Post(this);
    }

    public static int getGoldPrice(){
        return 10;
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
    protected BuildSlot[] initSlots() {
        double width = getImage().getWidth();
        double height = getImage().getHeight();

        return new BuildSlot[]{
            new BuildSlot(this, x, y),
            new BuildSlot(this, x + width, y),
            new BuildSlot(this, x, y + height),
            new BuildSlot(this, x + width, y + height),
        };
    }

}
