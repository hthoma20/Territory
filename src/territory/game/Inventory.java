package territory.game;

import territory.util.GlobalConstants;
import territory.game.construction.Post;
import territory.game.construction.Village;
import territory.game.construction.Wall;
import territory.game.unit.Unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Inventory implements Serializable {

    private List<Unit> units = new ArrayList<>();
    private List<Village> villages = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();

    private int gold = GlobalConstants.INITIAL_GOLD;
    private int stone = GlobalConstants.INITIAL_STONE;
    private int wood = GlobalConstants.INITIAL_WOOD;

    public Inventory(){

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

    public void removeUnit(Unit unit){
        units.remove(unit);

        //update indices
        for(int i = 0; i < units.size(); i++){
            units.get(i).setIndex(i);
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
     * Add wood to the inventory, does not subtract
     * @param wood the amount of wood to add
     * @return whether or not the wood was added, equivalently
     *         whether the argument was non-negative
     */
    public boolean giveWood(int wood){
        if(wood < 0){
            return false;
        }

        this.wood += wood;
        return true;
    }

    /**
     * Substract the wood and return true if possible,
     * otherwise return false
     * @return whether or not the wood was taken, equivalently, whether or not
     *          the player has sufficient funds
     */
    public boolean takeWood(int wood){
        if(this.wood < wood){
            return false;
        }

        this.wood -= wood;
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

    public int getWood(){
        return wood;
    }
}
