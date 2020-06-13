package territory.joiner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import territory.game.Game;
import territory.game.RemoteGame;
import territory.game.Version;
import territory.game.info.GameStartedInfo;
import territory.game.info.JoinInfo;
import territory.game.info.PlayerAddedInfo;
import territory.game.player.GUIPlayer;
import territory.game.player.Player;
import territory.gui.component.SwapPane;
import territory.joiner.request.JoinRoomRequest;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
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
    //force a version failure for testing and debugging
    private static final boolean FORCE_VERSION_FAILURE = false;

    @FXML private SwapPane swapPane;
    @FXML private Pane roomsPane;

    @FXML private Pane currentRoomPane;
    @FXML private Button startButton;
    @FXML private Text playerListText;

    @FXML private Pane versionErrorPane;
    @FXML private Pane versionUpdatedPane;
    @FXML private Label clientVersionLabel;
    @FXML private Label serverVersionLabel;

    private String remoteHost;
    private int remotePort;

    private GUIPlayer player;

    private Consumer<Game> onGameStart;

    public void init(String remoteHost, int remotePort, GUIPlayer player) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.player = player;
    }

    public void start() {

        //check versions
        Version serverVersion = (Version) remoteObjectRequest("/current_version");

        if(serverVersion.equals(Version.CURRENT_VERSION) && !FORCE_VERSION_FAILURE){
            displayGameRooms();
        }
        else{
            displayVersionError(serverVersion, Version.CURRENT_VERSION);
        }
    }

    private void displayVersionError(Version serverVersion, Version clientVersion){
        serverVersionLabel.setText(serverVersion.toString());
        clientVersionLabel.setText(clientVersion.toString());

        swapPane.show(versionErrorPane);
    }

    @FXML
    public void proceedWithVersion(ActionEvent e){
        displayGameRooms();
    }

    @FXML
    public void updateVersion(ActionEvent e){
        System.out.println("Request to update version");
        remoteFileRequest("/client_jar", "./Territory.jar");
        swapPane.show(versionUpdatedPane);
    }

    private void displayGameRooms() {
        List<GameRoom> gameRooms = (List<GameRoom>) remoteObjectRequest("/games");

        if(AUTO_START){
            GameRoom room = gameRooms.get(0);
            joinRoom(room);
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startGame(room);
            return;
        }

        swapPane.show(roomsPane);

        roomsPane.getChildren().clear();

        for(GameRoom room : gameRooms){
            roomsPane.getChildren().add(joinGameRoomNode(room));
        }
    }

    private Node joinGameRoomNode(GameRoom room){
        Button roomButton = new Button(String.format("Room %d", room.getRoomId()));
        roomButton.setOnMouseClicked(event -> {
            joinRoom(room);
        });

        return roomButton;
    }

    private void joinRoom(GameRoom room) {
        JoinRoomRequest request = new JoinRoomRequest(room.getRoomId(), Player.randomName());
        int port = (Integer) remoteObjectRequest("/join", request);
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
        remoteObjectRequest("/start_game", room.getRoomId());
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

    private Object remoteObjectRequest(String file, Object requestObject)  {
        try {
            URL url = new URL("http", remoteHost, remotePort, file);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            if (requestObject != null) {
                connection.setDoOutput(true);
                connection.getOutputStream().write(getBytes(requestObject));
            }

            connection.connect();

            Object obj = null;

            try (ObjectInputStream in = new ObjectInputStream(connection.getInputStream())) {
                obj = in.readObject();
            }
//        catch(SocketException ignored){
//            ignored.printStackTrace();
//        }

            connection.disconnect();

            return obj;
        }
        catch(IOException | ClassNotFoundException exc){
            exc.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    private Object remoteObjectRequest(String file) {
        return remoteObjectRequest(file, null);
    }

    /**
     * Download and save a file
     * @param remoteFile the file to download
     * @param localFile the path to save to
     */
    private void remoteFileRequest(String remoteFile, String localFile) {
        try{
            URL url = new URL("http", remoteHost, remotePort, remoteFile);

            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            FileOutputStream fis = new FileOutputStream(localFile);
            byte[] buffer = new byte[1024];
            int count=0;
            while((count = bis.read(buffer,0,1024)) != -1)
            {
                fis.write(buffer, 0, count);
            }
            fis.close();
            bis.close();
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }
}
