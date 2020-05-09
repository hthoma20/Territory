package territory.joiner;

import territory.game.LocalGame;
import territory.game.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameRoom implements Serializable {
    private transient List<Player> players = new ArrayList<>();

    private boolean gameInProgress = false;

    private static int nextId = 0;
    private int roomId = nextId++;

    public void addPlayer(Player player){
        this.players.add(player);
    }

    public List<Player> getPlayers(){
        return players;
    }

    private Player[] getPlayerArray(){
        return players.toArray(new Player[0]);
    }

    public int numPlayers(){
        return players.size();
    }

    public int getRoomId() {
        return roomId;
    }

    public void startGame(){
        if(gameInProgress){
            throw new JoinerException("Game already started");
        }

        LocalGame game = new LocalGame(getPlayerArray());
        game.start();
        gameInProgress = true;
    }
}
