package territory.game.action.player;

import territory.game.GameColor;
import territory.game.construction.upgrade.VillageUpgrade;
import territory.game.info.PlayerAddedInfo;

import java.io.Serializable;

public class UpgradeVillageAction extends PlayerAction implements Serializable {
    private int villageIndex;
    private VillageUpgrade upgrade;

    public UpgradeVillageAction(GameColor color, int villageIndex, VillageUpgrade upgrade) {
        super(color);

        this.villageIndex = villageIndex;
        this.upgrade = upgrade;
    }

    public int getVillageIndex() {
        return villageIndex;
    }

    public VillageUpgrade getUpgrade() {
        return upgrade;
    }
}
