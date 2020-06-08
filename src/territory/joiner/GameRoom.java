package territory.joiner;

import territory.game.LocalGame;
import territory.game.info.PlayerAddedInfo;
import territory.game.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameRoom implements Serializable {
    private transient List<Player> players = new ArrayList<>();

    private boolean gameInProgress = false;

    private static int nextId = 0;
    private int roomId = nextId++;

    public void addPlayer(Player newPlayer){
        this.players.add(newPlayer);

        List<String> playerNames = players.stream().map(Player::getName).collect(Collectors.toList());
        for(Player player : this.players){
            player.sendInfo(new PlayerAddedInfo(playerNames));
        }
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
