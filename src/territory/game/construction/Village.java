package territory.game.construction;

import territory.game.Copyable;
import territory.game.GameColor;
import territory.game.Indexable;
import territory.game.player.Player;
import territory.game.sprite.ImageSprite;

public class Village extends ImageSprite implements Copyable<Village>, Indexable {

    private Player owner;

    private int population = 10;

    private int index = -1;

    public Village(Player owner, double x, double y){
        super(x, y);
        this.owner = owner;
    }

    public Village(Village src){
        super(src);
        this.owner = src.owner;
        this.index = src.index;
        this.population = src.population;
    }

    @Override
    public Village copy(){
        return new Village(this);
    }

    @Override
    public GameColor getColor() {
        return owner.getColor();
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
