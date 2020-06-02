package territory.game;


import territory.game.construction.Construction;
import territory.game.construction.Mine;
import territory.game.construction.Wall;
import territory.game.construction.WallSegment;
import territory.game.player.Player;
import territory.game.sprite.Sprite;
import territory.game.target.PatrolArea;
import territory.game.unit.Soldier;
import territory.game.unit.Unit;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class GameState implements Copyable<GameState>, Serializable {

    private int numPlayers;
    private List<Inventory> playerInventories;
    private List<TerritoryList> playerTerritories;
    private List<Mine> mines;

    public GameState(int numPlayers){
        this.numPlayers = numPlayers;

        initInventories();
        initTerritories();
        initMines();
    }

    public GameState(GameState src){
        this.numPlayers = src.numPlayers;

        this.playerInventories = new ArrayList<>(src.playerInventories.size());
        for(Inventory inventory : src.playerInventories){
            this.playerInventories.add(inventory.copy());
        }

        this.playerTerritories = new ArrayList<>(src.playerTerritories.size());
        for(TerritoryList territoryList : src.playerTerritories){
            this.playerTerritories.add(territoryList.copy());
        }

        this.mines = new ArrayList<>(src.mines.size());
        for(Mine srcMine : src.mines){
            this.mines.add(srcMine.copy());
        }
    }

    @Override
    public GameState copy() {
        return new GameState(this);
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
        this.mines = new ArrayList<>();

        mines.add(new Mine(0,0));

        for(int i = 0; i < mines.size(); i++){
            mines.get(i).setIndex(i);
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
        sprites.addAll(getAllWallSegments());

        for(Inventory inventory : playerInventories){
            sprites.addAll(inventory.getPosts());

            sprites.addAll(inventory.getVillages());
            sprites.addAll(inventory.getUnits());
        }

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
        List<Unit> units = new ArrayList<>();

        for(Inventory inventory : playerInventories){
            for(Unit unit : inventory.getUnits()){
                if(area.contains(unit.getX(), unit.getY())){
                    units.add(unit);
                }
            }
        }

        return units;
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

    public List<Sprite> getSpritesContaining(double x, double y){
        return getAllSprites().stream().filter(sprite -> sprite.containsPoint(x, y))
                .collect(Collectors.toList());
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

    public GameColor[] colorsInUse(){
        return Arrays.copyOf(GameColor.values(), numPlayers);
    }
}
