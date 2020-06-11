package territory.game.construction;

import territory.game.GameColor;
import territory.game.Indexable;
import territory.game.sprite.ImageStore;
import territory.game.target.BuildSlot;

import java.io.Serializable;

public class Post extends Buildable implements Construction, Indexable, Serializable {

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
    public double getBuildZoneRadius(){
        //post width + wall segment width
        return ImageStore.store.imageFor(this, color).getWidth() +
                ImageStore.store.imageFor(WallSegment.class, color).getWidth();
    }

    @Override
    protected BuildSlot[] initSlots() {
        double xRad = getWidth()/2;
        double yRad = getHeight()/2;

        return new BuildSlot[]{
            new BuildSlot(this, x-xRad , y-yRad),
            new BuildSlot(this, x-xRad, y+yRad),
            new BuildSlot(this, x+xRad, y-yRad),
            new BuildSlot(this, x+xRad, y+yRad),
        };
    }

}
