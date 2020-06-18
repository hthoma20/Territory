package territory.game;


import javafx.geometry.Point2D;
import territory.game.construction.*;
import territory.game.player.Player;
import territory.game.sprite.Sprite;
import territory.game.target.PatrolArea;
import territory.game.unit.Soldier;
import territory.game.unit.Unit;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class GameState implements Serializable {

    private int numPlayers;
    private List<Inventory> playerInventories;
    private List<TerritoryList> playerTerritories;
    private List<Mine> mines;
    private List<Tree> trees;

    //the part of the map which can be played on
    private final double areaInPlayWidth = 1500;
    private final double areaInPlayHeight = 750;
    private RectangleArea areaInPlay =
            new RectangleArea(-areaInPlayWidth/2, -areaInPlayHeight/2, areaInPlayWidth/2, areaInPlayHeight/2);

    public GameState(int numPlayers){
        this.numPlayers = numPlayers;

        initInventories();
        initTerritories();
        initMines();
        initTrees();
    }

    private void initInventories(){
        this.playerInventories = new ArrayList<>(this.numPlayers);

        for(int i = 0; i < numPlayers; i++){
            playerInventories.add(new Inventory());
        }
    }

    private void initTerritories(){
        this.playerTerritories = new ArrayList<>(this.numPlayers);

        for(int i = 0; i < numPlayers; i++){
            playerTerritories.add(new TerritoryList());
        }
    }

    private void initMines(){
        int numMines = numPlayers + RNG.randInt(2);

        this.mines = new ArrayList<>(numMines);

        double padding = 200;
        double minX = areaInPlay.getTopX() + padding;
        double maxX = areaInPlay.getTopX() + areaInPlay.getWidth() - padding;
        double minY = areaInPlay.getTopY() + padding;
        double maxY = areaInPlay.getTopY() + areaInPlay.getHeight() - padding;

        while(mines.size() < numMines){
            double x = RNG.randDouble(minX, maxX);
            double y = RNG.randDouble(minY, maxY);

            //check that its a valid spot
            boolean valid = true;
            for(Mine mine : mines){
                if(new Point2D(x, y).distance(mine.getX(), mine.getY()) < mine.getBuildZoneRadius()){
                    valid = false;
                    break;
                }
            }

            if(valid){
                mines.add(new Mine(x, y));
            }
        }

        //set mine indices
        for(int i = 0; i < mines.size(); i++){
            mines.get(i).setIndex(i);
        }
    }

    private void initTrees(){
        int numTrees = 100*numPlayers + RNG.randInt(100);

        this.trees = new ArrayList<>(numTrees);

        double padding = 20;
        double minX = areaInPlay.getTopX() + padding;
        double maxX = areaInPlay.getTopX() + areaInPlay.getWidth() - padding;
        double minY = areaInPlay.getTopY() + padding;
        double maxY = areaInPlay.getTopY() + areaInPlay.getHeight() - padding;

        while(trees.size() < numTrees){
            double x = RNG.randDouble(minX, maxX);
            double y = RNG.randDouble(minY, maxY);

            //check that its a valid spot
            boolean valid = true;
            for(Mine mine : mines){
                if(new Point2D(x, y).distance(mine.getX(), mine.getY()) < mine.getBuildZoneRadius()){
                    valid = false;
                    break;
                }
            }

            if(valid){
                trees.add(new Tree(x, y));
            }
        }


        //set tree indices
        for(int i = 0; i < trees.size(); i++){
            this.trees.get(i).setIndex(i);
        }
    }

    public List<Tickable> getAllTickables(){
        List<Tickable> tickables = new ArrayList<>();
        tickables.addAll(getAllSprites());
        tickables.addAll(getAllWalls());
        tickables.addAll(getAllPatrolAreas());
        return tickables;
    }

    public List<Sprite> getAllSprites(){
        List<Sprite> sprites = new ArrayList<>();

        sprites.addAll(mines);

        sprites.addAll(trees);

        sprites.addAll(getAllWallSegments());

        sprites.addAll(getAllPosts());

        sprites.addAll(getAllVillages());

        sprites.addAll(getAllUnits());

        return sprites;
    }

    /**
     * @param area the area to get sprites within
     * @return a list of all sprites in the given area
     */
    public List<Sprite> getAllSpritesInArea(RectangleArea area){
        return getAllSprites().stream().filter( sprite ->
                area.contains(sprite.getX(), sprite.getY()))
                .collect(Collectors.toList());
    }

    public List<Unit> getAllUnitsInArea(RectangleArea area){
        return getAllUnits().stream().filter(unit ->
                area.contains(unit.getX(), unit.getY()))
                .collect(Collectors.toList());
    }

    public List<Construction> getAllConstructions(){
        List<Construction> constructions = new ArrayList<>();

        constructions.addAll(mines);
        constructions.addAll(getAllWallSegments());

        for(Inventory inventory : playerInventories){
            constructions.addAll(inventory.getPosts());
            constructions.addAll(inventory.getVillages());
        }

        return constructions;
    }

    public List<WallSegment> getAllWallSegments(){
        ArrayList<WallSegment> segments = new ArrayList<>();

        for(Wall wall : getAllWalls()){
            segments.addAll(Arrays.asList(wall.getSegments()));
        }

        return segments;
    }

    public List<Wall> getAllWalls(){
        List<Wall> walls = new ArrayList<>();

        for(Inventory inventory : playerInventories){
            walls.addAll(inventory.getWalls());
        }

        return walls;
    }

    public Set<PatrolArea> getAllPatrolAreas(){
        HashSet<PatrolArea> patrolAreas = new HashSet<>();

        for(Inventory inventory : playerInventories){
            for(Unit unit : inventory.getUnits()){
                if(unit instanceof Soldier){
                    PatrolArea area = ((Soldier) unit).getPatrolArea();
                    if(area != null){
                        patrolAreas.add(area);
                    }
                }
            }
        }

        return patrolAreas;
    }

    public List<Post> getAllPosts(){
        ArrayList<Post> posts = new ArrayList<>();
        for(Inventory inventory : playerInventories){
            posts.addAll(inventory.getPosts());
        }

        return posts;
    }

    public List<Village> getAllVillages(){
        ArrayList<Village> villages = new ArrayList<>();
        for(Inventory inventory : playerInventories){
            villages.addAll(inventory.getVillages());
        }

        return villages;
    }

    public List<Unit> getAllUnits(){
        ArrayList<Unit> units = new ArrayList<>();
        for(Inventory inventory : playerInventories){
            units.addAll(inventory.getUnits());
        }

        return units;
    }

    /**
     * @param color the color of the unit to get collidables for
     * @return a list of all sprites that a unit of the given color cannot move through
     */
    public List<Sprite> getAllCollidables(GameColor color){
        ArrayList<Buildable> collidables = new ArrayList<>();
        collidables.addAll(getAllWallSegments());
        collidables.addAll(getAllPosts());

        //a unit can only collide with a different color, and it must be fully built
        return collidables.stream().filter(collidable ->
            collidable.isComplete() && collidable.getColor() != color
        ).collect(Collectors.toList());
    }

    public List<Sprite> getSpritesContaining(double x, double y){
        return getAllSprites().stream().filter(sprite -> sprite.containsPoint(x, y))
                .collect(Collectors.toList());
    }

    public List<Sprite> getSpritesContaining(Point2D point){
        return getSpritesContaining(point.getX(), point.getY());
    }

    public Inventory getPlayerInventory(int playerIndex){
        return playerInventories.get(playerIndex);
    }

    public Inventory getPlayerInventory(Player player){
        return getPlayerInventory(player.getIndex());
    }

    public Inventory getPlayerInventory(GameColor color){
        return getPlayerInventory(color.ordinal());
    }

    public List<Inventory> getPlayerInventories(){
        return playerInventories;
    }

    public List<TerritoryList> getPlayerTerritories(){
        return playerTerritories;
    }

    public TerritoryList getPlayerTerritories(int index){
        return playerTerritories.get(index);
    }

    public void setPlayerTerritories(int index, TerritoryList territories){
        playerTerritories.set(index, territories);
    }

    public Mine getMine(int index){
        return mines.get(index);
    }

    public List<Tree> getTrees(){
        return trees;
    }

    public Tree getTree(int index){
        return trees.get(index);
    }

    public RectangleArea getAreaInPlay(){
        return areaInPlay;
    }

    public GameColor[] colorsInUse(){
        return Arrays.copyOf(GameColor.values(), numPlayers);
    }
}
