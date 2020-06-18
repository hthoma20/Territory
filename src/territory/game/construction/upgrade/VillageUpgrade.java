package territory.game.construction.upgrade;

import java.io.Serializable;

public enum VillageUpgrade implements Serializable {
    WELL(100, "Adds an aesthetic well to the village"),
    BARRACKS(100, "Enables this village to train Soldiers");

    //the amount of wood required to make this upgrade
    private int woodPrice;
    private String description;

    VillageUpgrade(int woodPrice, String description){
        this.woodPrice = woodPrice;
        this.description = description;
    }

    public int getWoodPrice(){
        return woodPrice;
    }

    public String getDescription() {
        return description;
    }
}
