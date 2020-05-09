package territory.joiner;

import com.sun.net.httpserver.HttpExchange;
import territory.game.player.RemotePlayer;

import java.io.InputStream;
import java.util.*;

public class GameJoiner {

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

    public List<GameRoom> getGameRooms(Object request) {
        System.out.println("Get game rooms");
        return new ArrayList<>(roomsById.values());
    }

    public Object getNumber(Object request){
        return 14;
    }


    public Object handleJoinRoomRequest(Object request){

        int roomId = (Integer)request;

        GameRoom room = roomsById.get(roomId);

        if(room == null){
            throw new JoinerException(String.format("No Room with id %d", roomId));
        }

        int port = getAvailablePort();
        RemotePlayer player = new RemotePlayer(port);

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

        room.startGame();

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
