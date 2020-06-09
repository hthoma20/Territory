package territory.game.target;

import territory.game.Copyable;
import territory.game.GameColor;
import territory.game.sprite.ImageSprite;

import java.io.Serializable;

public abstract class Buildable extends ImageSprite implements Copyable<Buildable>, Serializable {

    protected GameColor color;

    private BuildSlot[] slots;

    protected int stoneNeeded = 0;//100;

    public Buildable(GameColor color, double x, double y) {
        super(x, y);

        this.color = color;

        this.slots = initSlots();
    }

    public Buildable(Buildable src) {
        super(src);

        this.color = src.color;

        this.slots = new BuildSlot[src.slots.length];
        for(int i = 0; i < src.slots.length; i++){
            this.slots[i] = src.slots[i].copy();
        }

        this.stoneNeeded = src.stoneNeeded;
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
}
