package territory.joiner;

import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import territory.game.GameNotStartedException;
import territory.game.RemoteGame;
import territory.game.player.GUIPlayer;
import territory.gui.Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.List;

import static territory.joiner.ObjectBytes.getBytes;

public class JoinerMain extends Application {

    public static final int PORT = 4000;
    public static final String HOST = "localhost";

    private double initialWidth = 605, initialHeight = 405;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        startServer();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/layout.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Territory");

        //exit on close
        primaryStage.setOnCloseRequest(this::exitApplication);

        primaryStage.setScene(new Scene(root, initialWidth, initialHeight));
        primaryStage.show();

        Controller controller = loader.getController();
        controller.init();

        GUIPlayer guiPlayer = new GUIPlayer(controller);

        startClient(guiPlayer);
    }

    public void startServer() throws IOException {
        GameJoiner joiner = new GameJoiner();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/games", new HttpObjectHandler(joiner::getGameRooms));
        server.createContext("/join", new HttpObjectHandler(joiner::handleJoinRoomRequest));
        server.createContext("/start_game", new HttpObjectHandler(joiner::handleStartGameRequest));

        server.start();
        System.out.println("Started on port: " + PORT);
    }

    public void startClient(GUIPlayer player) throws IOException, ClassNotFoundException {
        List<GameRoom> rooms = (List<GameRoom>) remoteRequest("/games");

        GameRoom room = rooms.get(0);
        System.out.println(room);

        int port = (Integer) remoteRequest("/join", room.getRoomId());
        System.out.println(port);

        RemoteGame game = new RemoteGame(player, HOST, port);
        game.connect();

        remoteRequest("/start_game", room.getRoomId());
    }

    public static Object remoteRequest(String file, Object requestObject) throws IOException, ClassNotFoundException {
        URL url = new URL("http", HOST, PORT, file);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        if(requestObject != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(getBytes(requestObject));
        }

        connection.connect();

        ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
        Object obj = in.readObject();

        in.close();
        connection.disconnect();

        return obj;
    }

    public static Object remoteRequest(String file) throws IOException, ClassNotFoundException {
        return remoteRequest(file, null);
    }

    private void exitApplication(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
    }
}
