package territory.game.action.player;

import territory.game.GameColor;
import territory.game.construction.upgrade.VillageUpgrade;
import territory.game.construction.upgrade.WorkShopItem;

import java.io.Serializable;

public class UpgradeWorkShopAction extends PlayerAction implements Serializable {
    private int villageIndex;
    private WorkShopItem upgrade;

    public UpgradeWorkShopAction(GameColor color, int villageIndex, WorkShopItem upgrade) {
        super(color);

        this.villageIndex = villageIndex;
        this.upgrade = upgrade;
    }

    public int getVillageIndex() {
        return villageIndex;
    }

    public WorkShopItem getUpgrade() {
        return upgrade;
    }
}
