package territory.game.construction;

import javafx.scene.canvas.GraphicsContext;
import territory.util.GlobalConstants;
import territory.game.GameColor;
import territory.game.sprite.ImageSprite;
import territory.game.target.BuildSlot;

import java.io.Serializable;

public abstract class Buildable extends ImageSprite implements Serializable {

    protected GameColor color;

    private BuildSlot[] slots;

    protected int stoneNeeded = GlobalConstants.BUILDABLE_STONE_NEEDED;

    public Buildable(GameColor color, double x, double y) {
        super(x, y);

        this.color = color;

        this.slots = initSlots();
    }

    /**
     * @return whether or not the object is done being built
     */
    public boolean isComplete(){
        return stoneNeeded <= 0;
    }

    /**
     * Give stone to this buildable
     * @param stone how much stone to give
     * @return the amount of stone taken
     */
    public int giveStone(int stone) {
        //if they are giving more stone than we need, don't take it all
        if(stone > this.stoneNeeded){
            int prevNeeded = this.stoneNeeded;
            this.stoneNeeded = 0;
            return prevNeeded;
        }

        //otherwise take all they give
        this.stoneNeeded -= stone;
        return stone;
    }

    @Override
    public GameColor getColor() {
        return color;
    }

    protected abstract BuildSlot[] initSlots();

    /**
     * @return the build slot with the least units building here already
     */
    public BuildSlot getOpenBuildSlot(){
        BuildSlot minSlot = slots[0];

        for(BuildSlot slot : slots){
            if(slot.getUnitCount() < minSlot.getUnitCount()){
                minSlot = slot;
            }
        }

        return minSlot;
    }

    @Override
    public void paintOn(GraphicsContext gc){
        double originalAlpha = gc.getGlobalAlpha();

        if(!isComplete()){
            gc.setGlobalAlpha(.5);
        }

        super.paintOn(gc);

        gc.setGlobalAlpha(originalAlpha);
    }
}
