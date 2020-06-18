package territory.game.construction;

import javafx.geometry.Point2D;
import territory.game.GameColor;
import territory.game.Indexable;
import territory.game.RNG;
import territory.game.sprite.ImageSprite;
import territory.game.sprite.ImageStore;
import territory.game.target.MineSlot;

import java.io.Serializable;

public class Mine extends ImageSprite implements Construction, Indexable, Serializable {

    private int index = -1;

    private MineSlot[] slots;

    private double goldProbability = .1;
    private double stoneProbability = .5;

    public Mine(double x, double y){
        super(x, y);
        initSlots();
    }

    private void initSlots(){

        //points relative to top-left
        Point2D[] slotPoints = new Point2D[]{
                new Point2D(17, 10),
                new Point2D(57, 22),
                new Point2D(53,51),
                new Point2D(19,53)
        };

        //adjust points to be relative to center
        double xOffset = -getWidth()/2;
        double yOffset = -getHeight()/2;
        for(int i = 0; i < slotPoints.length; i++){
            slotPoints[i] = new Point2D(slotPoints[i].getX() + xOffset, slotPoints[i].getY() + yOffset);
        }

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
        return RNG.withProbability(goldProbability) ? 1 : 0;
    }

    /**
     * @return the amount of stone yielded on this mining attempt
     */
    public int getStone(){
        return RNG.withProbability(stoneProbability) ? 1 : 0;
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

    @Override
    public double getBuildZoneRadius(){
        return ImageStore.store.imageFor(this, null).getWidth();
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
