package game.construction;

import game.Copyable;
import game.GameColor;
import game.Indexable;
import game.player.Player;
import game.sprite.ImageSprite;

public class Post extends ImageSprite implements Copyable<Post>, Indexable, Buildable {

    private Player owner;

    private int index = -1;

    private int stoneNeeded = 100;

    public Post(Player owner, double x, double y){
        super(x, y);
        this.owner = owner;
    }

    public Post(Post src){
        super(src);

        this.owner = src.owner;
        this.index = src.index;
    }

    @Override
    public Post copy(){
        return new Post(this);
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
        return this.index;
    }

    @Override
    public GameColor getColor() {
        return owner.getColor();
    }

    @Override
    public boolean isComplete() {
        return stoneNeeded <= 0;
    }

    @Override
    public int giveStone(int stone) {
        //if they are giving more stone than we need, don't take it all
        if(stone > this.stoneNeeded){
            int prevNeeded = this.stoneNeeded;
            this.stoneNeeded = 0;
            return prevNeeded;
        }

        //otherwise take all they give
        this.stoneNeeded -= stone;
        return stone;
    }
}
