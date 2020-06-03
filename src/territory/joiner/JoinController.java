package territory.joiner;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import territory.game.Game;
import territory.game.RemoteGame;
import territory.game.info.GameStartedInfo;
import territory.game.info.JoinInfo;
import territory.game.player.GUIPlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import static territory.joiner.ObjectBytes.getBytes;

public class JoinController {
    //automatically join the first room and start the game
    private static final boolean AUTO_START = true;

    @FXML public HBox gamesBox;
    @FXML public HBox currentGameBox;

    private String remoteHost;
    private int remotePort;

    private static int requestCount = 0;

    private GUIPlayer player;

    private Consumer<Game> onGameStart;

    public void init(String remoteHost, int remotePort, GUIPlayer player) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.player = player;
    }

    public void start() throws IOException, ClassNotFoundException {
        List<GameRoom> rooms = (List<GameRoom>) remoteRequest("/games");

        if(AUTO_START){
            GameRoom room = rooms.get(0);
            joinRoom(room);
            startGame(room);
            return;
        }

        displayGameRooms(rooms);
    }

    private void displayGameRooms(List<GameRoom> gameRooms){
        currentGameBox.setVisible(false);
        gamesBox.setVisible(true);

        gamesBox.getChildren().clear();

        for(GameRoom room :gameRooms){
            gamesBox.getChildren().add(joinGameRoomNode(room));
        }
    }

    private Node joinGameRoomNode(GameRoom room){
        Button roomButton = new Button(String.format("Room %d", room.getRoomId()));
        roomButton.setOnMouseClicked(event -> {
            try {
                joinRoom(room);
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        return roomButton;
    }

    private void joinRoom(GameRoom room) throws IOException, ClassNotFoundException {
        int port = (Integer) remoteRequest("/join", room.getRoomId());
        System.out.println(port);

        RemoteGame game = new RemoteGame(player, remoteHost, port);

        game.setJoinInfoListener(info -> {
            if(info instanceof GameStartedInfo && onGameStart != null){
                onGameStart.accept(game);
            }
        });

        game.connect();

        displayRoom(room, game);
    }

    private void displayRoom(GameRoom room, Game game){
        gamesBox.setVisible(false);
        currentGameBox.setVisible(true);

        Button startButton = new Button("Start game");

        startButton.setOnMouseClicked(event -> startGame(room));

        currentGameBox.getChildren().add(startButton);

    }

    //send a start game request
    private void startGame(GameRoom room){
        try {
            remoteRequest("/start_game", room.getRoomId());
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onGameStart(Consumer<Game> onGameStart){
        this.onGameStart = onGameStart;
    }

    private Object remoteRequest(String file, Object requestObject) throws IOException, ClassNotFoundException {
        URL url = new URL("http", remoteHost, remotePort, file);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        if(requestObject != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(getBytes(requestObject));
        }

        connection.connect();

        Object obj = null;

        try(ObjectInputStream in = new ObjectInputStream(connection.getInputStream())){
            obj = in.readObject();
        }
//        catch(SocketException ignored){
//            ignored.printStackTrace();
//        }

        connection.disconnect();

        return obj;
    }

    private Object remoteRequest(String file) throws IOException, ClassNotFoundException {
        return remoteRequest(file, null);
    }
}
