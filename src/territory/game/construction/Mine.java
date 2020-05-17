package territory.game.construction;

import javafx.geometry.Point2D;
import territory.game.Copyable;
import territory.game.GameColor;
import territory.game.Indexable;
import territory.game.RNG;
import territory.game.sprite.ImageSprite;
import territory.game.target.MineSlot;

import java.io.Serializable;

public class Mine extends ImageSprite implements Copyable<Mine>, Indexable, Serializable {

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

        Point2D[] slotPoints = new Point2D[]{
                new Point2D(17, 10),
                new Point2D(57, 22),
                new Point2D(53,51),
                new Point2D(19,53)
        };

        slots = new MineSlot[slotPoints.length];
        for(int i = 0; i < slots.length; i++){
            Point2D slotPoint = slotPoints[i];
            slots[i] = new MineSlot(this, x + slotPoint.getX(), y + slotPoint.getY());
        }
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
