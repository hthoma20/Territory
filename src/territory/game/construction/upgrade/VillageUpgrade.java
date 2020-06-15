package territory.game.construction.upgrade;

import java.io.Serializable;

public enum VillageUpgrade implements Serializable {
    WELL(100),
    BARRACKS(100);

    //the amount of wood required to make this upgrade
    private int woodPrice;

    VillageUpgrade(int woodPrice){
        this.woodPrice = woodPrice;
    }

    public int getWoodPrice(){
        return woodPrice;
    }
}
