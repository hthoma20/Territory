package territory.game;


import territory.game.construction.Mine;
import territory.game.construction.Wall;
import territory.game.player.Player;
import territory.game.sprite.Sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameState implements Copyable<GameState>, Serializable {

    private int numPlayers;
    private List<Inventory> playerInventories;
    private List<Mine> mines;

    public GameState(int numPlayers){
        this.numPlayers = numPlayers;
        initInventories();
        initMines();
    }

    public GameState(GameState src){
        this.numPlayers = src.numPlayers;

        this.playerInventories = new ArrayList<>(src.playerInventories.size());
        for(Inventory inventory : src.playerInventories){
            this.playerInventories.add(inventory.copy());
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

    private void initMines(){
        this.mines = new ArrayList<>();

        mines.add(new Mine(0, 0));

        for(int i = 0; i < mines.size(); i++){
            mines.get(i).setIndex(i);
        }
    }

    public List<Tickable> getAllTickables(){
        List<Tickable> tickables = new ArrayList<>();
        tickables.addAll(getAllSprites());
        return tickables;
    }

    public List<Sprite> getAllSprites(){
        List<Sprite> sprites = new ArrayList<>();

        sprites.addAll(mines);

        for(Inventory inventory : playerInventories){
            for(Wall wall : inventory.getWalls()){
                sprites.addAll(Arrays.asList(wall.getSegments()));
            }

            sprites.addAll(inventory.getPosts());
            sprites.addAll(inventory.getVillages());
            sprites.addAll(inventory.getUnits());
        }

        return sprites;
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

    public List<Inventory> getPlayerInventories(){
        return playerInventories;
    }

    public Mine getMine(int index){
        return mines.get(index);
    }
}
