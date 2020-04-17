package game;


import game.player.Player;
import game.sprite.Sprite;

import java.util.ArrayList;
import java.util.Collection;

public class GameState {
    private int numPlayers;
    private Inventory[] playerInventories;

    public GameState(int numPlayers){
        this.numPlayers = numPlayers;
        initInventories();
    }

    private void initInventories(){
        playerInventories = new Inventory[this.numPlayers];

        for(int i = 0; i < playerInventories.length; i++){
            playerInventories[i] = new Inventory();
        }
    }

    public Collection<Sprite> getAllSprites(){
        Collection<Sprite> sprites = new ArrayList<>();

        for(Inventory inventory : playerInventories){
            sprites.addAll(inventory.getAllUnits());
            sprites.addAll(inventory.getVillages());
        }

        return sprites;
    }

    public Inventory getPlayerInventory(int playerIndex){
        return playerInventories[playerIndex];
    }

    public Inventory getPlayerInventory(Player player){
        return getPlayerInventory(player.getIndex());
    }

    public Inventory[] getPlayerInventories(){
        return playerInventories;
    }
}
