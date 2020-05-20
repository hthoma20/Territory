package territory.game.construction;

import territory.game.Copyable;
import territory.game.GameColor;
import territory.game.GameState;
import territory.game.Indexable;
import territory.game.action.tick.TickAction;
import territory.game.sprite.ImageSprite;
import territory.game.sprite.ImageStore;

import java.io.Serializable;
import java.util.List;

public class Village extends ImageSprite implements Construction, Copyable<Village>, Indexable, Serializable {

    private GameColor color;

    private int population = 10;

    //how many ticks per spawn
    private int spawnRate = 80;
    //how many ticks until spawn
    private int timeToSpawn = spawnRate;

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

        timeToSpawn--;
        if(timeToSpawn < 1){
            population++;
            timeToSpawn = spawnRate;
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
