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

    private int population = 5;

    //how many ticks per spawn
    private int spawnRate = 200;
    //how many ticks until spawn
    private int timeToSpawn = spawnRate;

    //how many ticks per gold
    private int goldRate = 200;
    //how many ticks until spawn
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
            timeToSpawn = spawnRate;
        }

        //increase gold
        timeToGold--;
        if(goldRate < 1){
            timeToGold = goldRate;
            return Arrays.asList(new GiveGoldAction(color, 1));
        }

        return null;
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
        return true;
    }
}
