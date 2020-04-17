package game;

import game.construction.Village;
import game.unit.Unit;

import java.util.ArrayList;
import java.util.List;

public class Inventory implements Copyable<Inventory>{
    private List<Unit> units = new ArrayList<>();
    private List<Village> villages = new ArrayList<>();

    private int gold = 50;
    private int stone = 0;

    public Inventory(){

    }

    public Inventory(Inventory src){
        this.units = new ArrayList<>(src.units.size());
        for(Unit srcUnit : src.units){
            this.units.add(srcUnit.copy());
        }

        this.villages = new ArrayList<>(src.villages.size());
        for(Village srcVillage : src.villages){
            this.villages.add(srcVillage.copy());
        }

        this.gold = src.gold;
        this.stone = src.stone;
    }

    @Override
    public Inventory copy(){
        return new Inventory(this);
    }

    public void addUnit(Unit unit){
        this.units.add(unit);

        //we just added this to the back of the list
        unit.setIndex(this.units.size()-1);
    }

    public void addVillage(Village village){
        this.villages.add(village);

        //we just added this to the back of the list
        village.setIndex(this.villages.size()-1);
    }

    public List<Unit> getUnits(){
        return units;
    }

    public List<Village> getVillages(){
        return villages;
    }

    public Unit getUnit(int index){
        return units.get(index);
    }

    public Village getVillage(int index){
        return villages.get(index);
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
