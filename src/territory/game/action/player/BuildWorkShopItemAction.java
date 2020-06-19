package territory.game.action.player;

import territory.game.GameColor;
import territory.game.construction.upgrade.WorkShopItem;

import java.io.Serializable;

public class BuildWorkShopItemAction extends PlayerAction implements Serializable {
    private int villageIndex;
    private WorkShopItem item;

    public BuildWorkShopItemAction(GameColor color, int villageIndex, WorkShopItem item) {
        super(color);

        this.villageIndex = villageIndex;
        this.item = item;
    }

    public int getVillageIndex() {
        return villageIndex;
    }

    public WorkShopItem getItem() {
        return item;
    }
}
