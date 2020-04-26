package territory.game.construction;

import territory.game.Copyable;
import territory.game.GameColor;
import territory.game.Indexable;
import territory.game.RNG;
import territory.game.sprite.ImageSprite;

public class Mine extends ImageSprite implements Copyable<Mine>, Indexable {

    private int index = -1;

    private MineSlot[] slots;

    public Mine(double x, double y){
        super(x, y);

        initSlots();
    }

    public Mine(Mine src){
        super(src);

        this.index = src.index;

        this.slots = new MineSlot[src.slots.length];
        for(int i = 0; i < src.slots.length; i++){
            this.slots[i] = src.slots[i].copy();
        }
    }

    @Override
    public Mine copy() {
        return new Mine(this);
    }

    private void initSlots(){
        double width = getImage().getWidth();
        double height = getImage().getHeight();

        slots = new MineSlot[]{
                new MineSlot(this, x, y),
                new MineSlot(this, x + width, y),
                new MineSlot(this, x, y + height),
                new MineSlot(this, x + width, y + height),
        };
    }

    /**
     * @return the amount of gold yielded on this mining attempt
     */
    public int getGold(){
        return RNG.withProbability(.5) ? 1 : 0;
    }

    /**
     * @return the amount of stone yielded on this mining attempt
     */
    public int getStone(){
        return RNG.withProbability(.8) ? 1 : 0;
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
    public GameColor getColor() {
        return null;
    }

    /**
     * Return an empty mine slot, or just an arbitrary one
     * if non are empty
     */
    public MineSlot getOpenMineSlot(){
        MineSlot minSlot = slots[0];

        for(MineSlot slot : slots){
            if(slot.getUnitCount() < minSlot.getUnitCount()){
                minSlot = slot;
            }
        }

        return minSlot;
    }
}
