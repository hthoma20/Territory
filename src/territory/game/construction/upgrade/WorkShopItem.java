package territory.game.construction.upgrade;

import territory.util.GlobalConstants;

/**
 * Things that can be built in a work shop
 */
public enum WorkShopItem {

    BOOTS(GlobalConstants.BOOTS_BENCH_WOOD, GlobalConstants.BOOTS_ITEM_WOOD, "Increase Units' movement speed"),
    ARMOR(GlobalConstants.ARMOR_BENCH_WOOD, GlobalConstants.ARMOR_ITEM_WOOD, "Increase Units' health");

    private int benchPrice;
    private int itemPrice;
    private String description;

    WorkShopItem(int benchPrice, int itemPrice, String description){
        this.benchPrice = benchPrice;
        this.itemPrice = itemPrice;
        this.description = description;
    }

    public int getBenchPrice(){
        return benchPrice;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public String getDescription() {
        return description;
    }
}
