package game;

import game.construction.Village;
import game.unit.Unit;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Unit> units = new ArrayList<>();
    private List<Village> villages = new ArrayList<>();

    private int gold = 50;
    private int stone = 0;

    public Inventory(){

    }

    public void addUnit(Unit unit){
        this.units.add(unit);
    }

    public void addVillage(Village village){
        this.villages.add(village);
    }

    public List<Unit> getAllUnits(){
        return units;
    }

    public List<Village> getVillages(){
        return villages;
    }

    /**
     * Substract the gold and return true if possible,
     * otherwise return false
     * @return whether or not the gold was taken, equivalently, whether or not
     *          the player has sufficient funds
     */
    public boolean takeGold(int gold){
        if(this.gold < gold){
            return false;
        }

        this.gold -= gold;
        return true;
    }

    public int getGold() {
        return gold;
    }

    public int getStone() {
        return stone;
    }
}
