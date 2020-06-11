package territory.game.construction;

import territory.game.*;
import territory.game.action.tick.GiveGoldAction;
import territory.game.action.tick.TickAction;
import territory.game.sprite.ImageSprite;
import territory.game.sprite.ImageStore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Village extends ImageSprite
                    implements Construction, Copyable<Village>, Indexable, Serializable {

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

    private int index = -1;

    public Village(GameColor color, double x, double y){
        super(x, y);
        this.color = color;
    }

    public Village(Village src){
        super(src);
        this.color = src.color;
        this.index = src.index;
        this.population = src.population;
    }

    @Override
    public Village copy(){
        return new Village(this);
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
}
