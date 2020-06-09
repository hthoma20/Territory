package territory.joiner;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import territory.game.Game;
import territory.game.RemoteGame;
import territory.game.info.GameStartedInfo;
import territory.game.info.JoinInfo;
import territory.game.info.PlayerAddedInfo;
import territory.game.player.GUIPlayer;
import territory.game.player.Player;
import territory.gui.SwapPane;
import territory.joiner.request.JoinRoomRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import static territory.joiner.ObjectBytes.getBytes;

public class JoinController {
    //automatically join the first room and start the game
    private static final boolean AUTO_START = true;

    @FXML private SwapPane swapPane;
    @FXML private Pane roomsPane;

    @FXML private Pane currentRoomPane;
    @FXML private Button startButton;
    @FXML private Text playerListText;

    private String remoteHost;
    private int remotePort;

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
        swapPane.show(roomsPane);

        roomsPane.getChildren().clear();

        for(GameRoom room : gameRooms){
            roomsPane.getChildren().add(joinGameRoomNode(room));
        }
    }

    private Node joinGameRoomNode(GameRoom room){
        Button roomButton = new Button(String.format("Room %d", room.getRoomId()));
        roomButton.setOnMouseClicked(event -> {
            try {
                joinRoom(room);
                System.out.println("Clicked");
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        return roomButton;
    }

    private void joinRoom(GameRoom room) throws IOException, ClassNotFoundException {
        JoinRoomRequest request = new JoinRoomRequest(room.getRoomId(), Player.randomName());
        int port = (Integer) remoteRequest("/join", request);
        System.out.println(port);

        RemoteGame game = new RemoteGame(player, remoteHost, port);

        game.setJoinInfoListener(info -> this.handleJoinInfo(info, game));

        game.connect();

        displayRoom(room, game);
    }

    private void handleJoinInfo(JoinInfo info, Game game){
        if(info instanceof GameStartedInfo){
            if(onGameStart != null) {
                onGameStart.accept(game);
            }
        }
        else if(info instanceof PlayerAddedInfo){
            displayNames(((PlayerAddedInfo) info).getPlayerNames());
        }
    }

    private void displayRoom(GameRoom room, Game game){
        swapPane.show(currentRoomPane);

        startButton.setOnMouseClicked(event -> startGame(room));
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

    /**
     * Set the text of the list of players
     * @param names the names to set
     */
    private void displayNames(List<String> names){
        StringBuilder namesText = new StringBuilder();
        for(String name : names){
            namesText.append(name).append("\n");
        }

        playerListText.setText(namesText.toString());
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
