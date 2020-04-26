package territory.game;

import territory.game.construction.Post;
import territory.game.construction.Village;
import territory.game.construction.Wall;
import territory.game.unit.Unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Inventory implements Copyable<Inventory>{
    private List<Unit> units = new ArrayList<>();
    private List<Village> villages = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();

    private int gold = 1000;
    private int stone = 1000;

    public Inventory(){

    }

    public Inventory(Inventory src){
        this.units = new ArrayList<>(src.units.size());
        for(Unit srcUnit : src.units){
            this.units.add(srcUnit.copy());
        }

        this.villages = new ArrayList<>(src.villages.size());
        for(Village srcVillage : src.villages){
            this.villages.add(srcVillage.copy());
        }

        this.posts = new ArrayList<>(src.posts.size());
        for(Post srcPost : src.posts){
            this.posts.add(srcPost.copy());
        }

        this.walls = new ArrayList<>(src.walls.size());
        for(Wall srcWall : src.walls){
            this.walls.add(srcWall.copy());
        }

        this.gold = src.gold;
        this.stone = src.stone;
    }

    @Override
    public Inventory copy(){
        return new Inventory(this);
    }

    public void addUnit(Unit unit){
        this.units.add(unit);

        //we just added this to the back of the list
        unit.setIndex(this.units.size()-1);
    }

    public void addUnits(Collection<Unit> units){
        for(Unit unit : units){
            addUnit(unit);
        }
    }

    public void addVillage(Village village){
        this.villages.add(village);

        //we just added this to the back of the list
        village.setIndex(this.villages.size()-1);
    }

    public void addPost(Post post){
        this.posts.add(post);

        //we just added this to the back of the list
        post.setIndex(this.posts.size()-1);
    }

    public void addWall(Wall wall){
        this.walls.add(wall);

        //we just added this to the back of the list
        wall.setIndex(this.walls.size()-1);
    }

    public List<Unit> getUnits(){
        return units;
    }

    public Unit getUnit(int index){
        return units.get(index);
    }

    public List<Village> getVillages(){
        return villages;
    }

    public Village getVillage(int index){
        return villages.get(index);
    }

    public List<Post> getPosts(){
        return posts;
    }

    public Post getPost(int index){
        return posts.get(index);
    }

    public List<Wall> getWalls(){
        return walls;
    }

    public Wall getWall(int index){
        return walls.get(index);
    }

    /**
     * Substract the gold and return true if possible,
     * otherwise return false
     * @return whether or not the gold was taken, equivalently, whether or not
     *          the player has sufficient funds
     */
    public boolean takeGold(int gold){
        if(this.gold < gold){
            return false;
        }

        this.gold -= gold;
        return true;
    }

    /**
     * Add gold to the inventory, does not subtract
     * @param gold the amount of gold to add
     * @return whether or not the gold was added, equivalently
     *         whether the argument was non-negative
     */
    public boolean giveGold(int gold){
        if(gold < 0){
            return false;
        }

        this.gold += gold;
        return true;
    }

    /**
     * Substract the stone and return true if possible,
     * otherwise return false
     * @return whether or not the stone was taken, equivalently, whether or not
     *          the player has sufficient funds
     */
    public boolean takeStone(int stone){
        if(this.stone < stone){
            return false;
        }

        this.stone -= stone;
        return true;
    }

    /**
     * Add stone to the inventory, does not subtract
     * @param stone the amount of stone to add
     * @return whether or not the stone was added, equivalently
     *         whether the argument was non-negative
     */
    public boolean giveStone(int stone){
        if(stone < 0){
            return false;
        }

        this.stone += stone;
        return true;
    }

    public int getGold() {
        return gold;
    }

    public int getStone() {
        return stone;
    }
}