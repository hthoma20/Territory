package territory.game.construction;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.TextAlignment;
import territory.game.*;
import territory.game.action.tick.GiveGoldAction;
import territory.game.action.tick.TickAction;
import territory.game.construction.upgrade.VillageUpgrade;
import territory.game.sprite.ImageSprite;
import territory.game.sprite.ImageStore;
import territory.game.unit.Soldier;
import territory.game.unit.Unit;

import java.io.Serializable;
import java.util.*;

public class Village extends ImageSprite implements Construction, Indexable, Serializable {

    private GameColor color;

    private int population = 2;

    //spawn time when there are 0 units
    private int firstUnitSpawnTime = 150;
    //how many ticks until spawn
    private int timeToSpawn = firstUnitSpawnTime;

    private double growthRate = 0.002;
    private int maxPopulation = 20;

    //how many ticks per gold
    private int goldRate = 200;
    //how many ticks until gold
    private int timeToGold = goldRate;

    private Set<VillageUpgrade> upgrades = new HashSet<>();

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
        return 10;
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
        if(hasUpgrade(VillageUpgrade.WELL)) {
            double wellXOffset = 22;
            double wellYOffset = 27;
            double wellX = topX + wellXOffset;
            double wellY = topY + wellYOffset;
            gc.drawImage(ImageStore.store.imageFor("Well"), wellX, wellY);
        }

        //paint the barracks
        if(hasUpgrade(VillageUpgrade.BARRACKS)){
            double barracksXOffset = 61;
            double barracksYOffset = -1;
            double barracksX = topX + barracksXOffset;
            double barracksY = topY + barracksYOffset;
            gc.drawImage(ImageStore.store.imageFor("Barracks"), barracksX, barracksY);
        }
    }

    public void upgrade(VillageUpgrade upgrade){
        this.upgrades.add(upgrade);
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
}
