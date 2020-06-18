package territory.joiner;

import territory.util.GlobalConstants;
import territory.game.Version;
import territory.game.info.GameStartedInfo;
import territory.game.player.ComputerPlayer;
import territory.game.player.Player;
import territory.game.player.RemotePlayer;
import territory.joiner.request.JoinRoomRequest;

import java.util.*;

public class GameJoiner {

    //add a computer player before a game is started (for testing and debugging)
    private static final boolean ADD_COMPUTER_PLAYER = GlobalConstants.ADD_COMPUTER_PLAYER;

    //map from room id to room
    private Map<Integer, GameRoom> roomsById = new HashMap<>();

    private int startPort = 4001;
    private int endPort = 4500;
    private Set<Integer> portsInUse = new HashSet<>();

    public GameJoiner(){
        addRoom();
    }

    private void addRoom(){
        GameRoom room = new GameRoom();
        roomsById.put(room.getRoomId(), room);
    }

    public Version getCurrentVersion(Object request){
        return Version.CURRENT_VERSION;
    }

    public List<GameRoom> getGameRooms(Object request) {
        System.out.println("Get game rooms");
        return new ArrayList<>(roomsById.values());
    }

    public Object handleJoinRoomRequest(Object requestObject){

        JoinRoomRequest request = (JoinRoomRequest)requestObject;
        int roomId = request.getRoomId();
        String playerName = request.getPlayerName();

        GameRoom room = roomsById.get(roomId);

        if(room == null){
            throw new JoinerException(String.format("No Room with id %d", roomId));
        }

        int port = getAvailablePort();
        RemotePlayer player = new RemotePlayer(port, playerName);

        new Thread(() -> {
            System.out.format("Connecting on port %d...\n", port);
            player.connect();
            System.out.format("Connected on port %d.\n", port);
            room.addPlayer(player);
        }).start();

        return port;
    }

    public Object handleStartGameRequest(Object request){
        int roomId = (Integer)request;

        GameRoom room = roomsById.get(roomId);

        if(room == null){
            throw new JoinerException(String.format("No Room with id %d", roomId));
        }

        //add a computer player (for testing and debugging)
        if(ADD_COMPUTER_PLAYER){
            System.out.println("Adding computer player");
            room.addPlayer(new ComputerPlayer());
        }

        System.out.println("Game joiner starting game");

        room.startGame();

        for(Player player : room.getPlayers()){
            player.sendInfo(new GameStartedInfo(String.format("Game in room %d started", roomId)));
        }

        System.out.println("Sent start game info to players");

        return null;
    }

    private int getAvailablePort(){
        int portToTry = startPort;

        while(portsInUse.contains(portToTry)){
            portToTry++;
            if(portToTry > endPort){
                throw new JoinerException("No available port");
            }
        }

        portsInUse.add(portToTry);
        return portToTry;
    }

    private void releasePort(int port){
        portsInUse.remove(port);
    }
}
