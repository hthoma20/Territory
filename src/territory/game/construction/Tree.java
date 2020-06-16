package territory.game.construction;

import javafx.scene.canvas.GraphicsContext;
import territory.game.Copyable;
import territory.game.GameColor;
import territory.game.Indexable;
import territory.game.sprite.ImageSprite;
import territory.game.target.Target;

import java.io.Serializable;


public class Tree extends ImageSprite
                    implements Construction, Indexable, Copyable<Tree>, Target, Serializable
{

    private int index = -1;

    private int wood = 10;
    private int health = 50;

    public Tree(double x, double y){
        super(x, y);
    }

    public Tree(Tree src){
        super(src);

        this.index = src.index;

        this.health = src.health;
    }

    public boolean isAlive(){
        return health > 0;
    }

    public void chop(int damage){
        health -= damage;
    }

    public int getWood(){
        return wood;
    }

    @Override
    public Tree copy(){
        return new Tree(this);
    }

    @Override
    public int getIndex(){
        return this.index;
    }

    @Override
    public void setIndex(int index){
        this.index = index;
    }

    @Override
    public double getBuildZoneRadius(){
        return getWidth();
    }

    @Override
    public GameColor getColor() {
        return null;
    }

    @Override
    public void paintOn(GraphicsContext gc){
        if(isAlive()){
            super.paintOn(gc);
        }
    }
}
