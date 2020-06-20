package territory.game.construction;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.TextAlignment;
import territory.game.*;
import territory.game.action.tick.GiveGoldAction;
import territory.game.action.tick.TickAction;
import territory.game.construction.upgrade.VillageUpgrade;
import territory.game.construction.upgrade.WorkShop;
import territory.game.construction.upgrade.WorkShopItem;
import territory.game.sprite.ImageSprite;
import territory.game.sprite.ImageStore;
import territory.game.unit.Soldier;
import territory.game.unit.Unit;
import territory.game.unit.stats.*;
import territory.util.GlobalConstants;

import java.io.Serializable;
import java.util.*;

public class Village extends ImageSprite implements Construction, Indexable, Serializable {

    private GameColor color;

    private int population = GlobalConstants.INITIAL_POPULATION;

    //spawn time when there are 0 units
    private int firstUnitSpawnTime = 150;
    //how many ticks until spawn
    private int timeToSpawn = firstUnitSpawnTime;

    private double growthRate = GlobalConstants.POPULATION_GROWTH_RATE;
    private int maxPopulation = GlobalConstants.MAX_POPULATION;

    //how many ticks per gold
    private int goldRate = GlobalConstants.TICKS_PER_GOLD;
    //how many ticks until gold
    private int timeToGold = goldRate;

    private Set<VillageUpgrade> upgrades = new HashSet<>();
    private WorkShop workShop = null;

    private int index = -1;

    public Village(GameColor color, double x, double y){
        super(x, y);
        this.color = color;
    }

    @Override
    public List<TickAction> tick(GameState currentState){

        //increase population
        timeToSpawn--;
        if(timeToSpawn < 1){

            population++;

            timeToSpawn = computeTimeToSpawn();
        }

        //increase gold
        timeToGold--;
        if(timeToGold < 1){
            timeToGold = goldRate;
            return Arrays.asList(new GiveGoldAction(color, 1));
        }

        return null;
    }

    /**
     * @return how long until the next unit should spawn, based on the current population
     */
    private int computeTimeToSpawn(){
        if(population == 0){
            return firstUnitSpawnTime;
        }

        double popPerTime = growthRate*(maxPopulation - population)/maxPopulation*population;
        return (int)(1.0/popPerTime);
    }

    @Override
    public GameColor getColor() {
        return color;
    }

    @Override
    public double getBuildZoneRadius(){
        return ImageStore.store.imageFor(this, color).getWidth();
    }

    public static int getGoldPrice(){
        return GlobalConstants.VILLAGE_GOLD;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public int getPopulation() {
        return population;
    }

    /**
     * Subtract population and return true if possible,
     * otherwise return false
     * @param population the population to subtract
     * @return whether the population was taken, equivalently whether there was a sufficient population
     */
    public boolean takePopulation(int population){
        if(this.population < population){
            return false;
        }

        this.population -= population;

        timeToSpawn = computeTimeToSpawn();

        return true;
    }

    @Override
    public void paintOn(GraphicsContext gc){
        super.paintOn(gc);

        paintUpgrades(gc);

        //paint the population
        double textY = y + getHeight()/2 + 15;
        gc.setTextAlign(TextAlignment.CENTER);
        gc.strokeText("Pop. " + population, x, textY);
    }

    private void paintUpgrades(GraphicsContext gc){
        double topX = getTopX();
        double topY = getTopY();

        //paint the well
        if(hasUpgrade(VillageUpgrade.TRADING_POST)) {
            double tradingPostXOffset = 5;
            double tradingPostYOffset = 5;
            double tradingPostX = topX + tradingPostXOffset;
            double tradingPostY = topY + tradingPostYOffset;
            gc.drawImage(ImageStore.store.imageFor("Trading_post"), tradingPostX, tradingPostY);
        }

        //paint the barracks
        if(hasUpgrade(VillageUpgrade.BARRACKS)){
            double barracksXOffset = 58;
            double barracksYOffset = 3;
            double barracksX = topX + barracksXOffset;
            double barracksY = topY + barracksYOffset;
            gc.drawImage(ImageStore.store.imageFor("Barracks"), barracksX, barracksY);
        }

        //paint the work shop
        if(hasUpgrade(VillageUpgrade.WORK_SHOP)){
            double workShopXOffset = 4;
            double workShopYOffset = 24;
            double workShopX = topX + workShopXOffset;
            double workShopY = topY + workShopYOffset;
            gc.drawImage(ImageStore.store.imageFor("Work_shop"), workShopX, workShopY);
        }
    }

    public void upgrade(VillageUpgrade upgrade){
        boolean added = this.upgrades.add(upgrade);

        //if we already had this upgrade, do nothing
        if(!added){
            return;
        }

        switch (upgrade){
            case TRADING_POST:
                this.goldRate = GlobalConstants.TICKS_PER_GOLD_WITH_TRADING_POST;
                return;
            case WORK_SHOP:
                this.workShop = new WorkShop();
                return;
        }
    }

    public boolean hasUpgrade(VillageUpgrade upgrade){
        return upgrades.contains(upgrade);
    }

    public List<VillageUpgrade> availableUpgrades(){
        List<VillageUpgrade> availableUpgrades = new LinkedList<>(Arrays.asList(VillageUpgrade.values()));
        availableUpgrades.removeAll(upgrades);
        return availableUpgrades;
    }

    public boolean canTrain(Class<? extends Unit> unitClass){
        //we can always spawn non-soldiers
        if(!unitClass.equals(Soldier.class)){
            return true;
        }

        //we can only spawn soldiers if we have a barracks
        return hasUpgrade(VillageUpgrade.BARRACKS);
    }


    /**
     * "Run the stats through the shop" by
     * adjusting the unit stats builder based on whats in the shop
     */
    private void alterUnitStats(UnitStats.Builder builder, double bootSpeed, int armorHealth){
        if(workShop.stock(WorkShopItem.BOOTS) > 0){
            workShop.takeItem(WorkShopItem.BOOTS);
            builder.speed(bootSpeed);
        }

        if(workShop.stock(WorkShopItem.ARMOR) > 0){
            workShop.takeItem(WorkShopItem.ARMOR);
            builder.health(armorHealth);
        }
    }

    /**
     * Based on the items in the shop, get the next miner's stats
     * Also spend the shop items
     * @return the stats that the next miner trained should have
     */
    public MinerStats nextMinerStats(){
        MinerStats.Builder builder = new MinerStats.Builder();

        if(workShop == null){
            return builder.build();
        }

        alterUnitStats(builder, GlobalConstants.BOOTS_MINER_SPEED, GlobalConstants.ARMOR_HEALTH);

        return builder.build();
    }

    public LumberjackStats nextLumberjackStats(){
        LumberjackStats.Builder builder = new LumberjackStats.Builder();

        if(workShop == null){
            return builder.build();
        }

        alterUnitStats(builder, GlobalConstants.BOOTS_LUMBERJACK_SPEED, GlobalConstants.ARMOR_HEALTH);

        return builder.build();
    }

    public BuilderStats nextBuilderStats(){
        BuilderStats.Builder builder = new BuilderStats.Builder();

        if(workShop == null){
            return builder.build();
        }

        alterUnitStats(builder, GlobalConstants.BOOTS_BUILDER_SPEED, GlobalConstants.ARMOR_HEALTH);

        return builder.build();
    }

    public SoldierStats nextSoldierStats(){
        SoldierStats.Builder builder = new SoldierStats.Builder();

        if(workShop == null){
            return builder.build();
        }

        alterUnitStats(builder, GlobalConstants.BOOTS_SOLDIER_SPEED, GlobalConstants.ARMOR_HEALTH);

        return builder.build();
    }

    public WorkShop getWorkShop() {
        return workShop;
    }
}
