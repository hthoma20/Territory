package territory.game.construction.upgrade;

import territory.util.GlobalConstants;

import java.io.Serializable;

public enum VillageUpgrade implements Serializable {
    BARRACKS(GlobalConstants.BARRACKS_WOOD, "Enables this village to train Soldiers"),
    TRADING_POST(GlobalConstants.TRADING_POST_WOOD, "Increases the rate at which this village earns Gold"),
    WORK_SHOP(GlobalConstants.WORK_SHOP_WOOD, "Allows you to build gear for your Units");

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
