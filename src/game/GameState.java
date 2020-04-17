package game;


import game.player.Player;
import game.sprite.Sprite;

import java.util.ArrayList;
import java.util.Collection;

public class GameState {
    private Player[] players;

    public GameState(Player... players){
        if(players.length > GameColor.values().length){
            throw new RuntimeException("Too many players.");
        }

        this.players = players;

        GameColor[] colors = GameColor.values();
        for(int i = 0; i < players.length; i++){
            this.players[i].setIndex(i);
            this.players[i].setColor(colors[i]);
        }
    }

    public Player[] getPlayers() {
        return players;
    }

    public Collection<Sprite> getAllSprites(){
        Collection<Sprite> sprites = new ArrayList<>();

        for(Player player : players){
            Inventory inventory = player.getInventory();
            sprites.addAll(inventory.getAllUnits());
            sprites.addAll(inventory.getVillages());
        }

        return sprites;
    }
}
