package territory.game.construction;

import territory.game.GameColor;
import territory.game.Indexable;
import territory.game.target.BuildSlot;
import territory.game.target.Buildable;

import java.io.Serializable;

public class Post extends Buildable implements Indexable, Serializable {

    private int index = -1;

    public Post(GameColor color, double x, double y){
        super(color, x, y);
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
