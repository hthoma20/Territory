package territory.joiner;

import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import territory.game.player.GUIPlayer;
import territory.gui.Controller;
import territory.joiner.handler.HttpFileHandler;
import territory.joiner.handler.HttpObjectHandler;

import java.io.IOException;
import java.net.*;

public class JoinerMain extends Application {

    public static final int PORT = 4000;
    public static final String HOST = "localhost";

    private double initialWidth = 1226, initialHeight = 657;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        startServer();

        FXMLLoader mainLayoutLoader = new FXMLLoader(getClass().getResource("../gui/layout.fxml"));
        Scene gameScene = new Scene(mainLayoutLoader.load(), initialWidth, initialHeight);

        FXMLLoader joinerLoader = new FXMLLoader(getClass().getResource("./joiner.fxml"));
        Scene joinerScene = new Scene(joinerLoader.load(), initialWidth, initialHeight);

        primaryStage.setTitle("Territory");

        //exit on close
        primaryStage.setOnCloseRequest(this::exitApplication);
        primaryStage.setResizable(false);

        primaryStage.setScene(joinerScene);
        primaryStage.show();

        Controller gameController = mainLayoutLoader.getController();
        gameController.init(gameScene);

        GUIPlayer guiPlayer = new GUIPlayer(gameController);

        JoinController joinController = joinerLoader.getController();

        joinController.init(HOST, PORT, guiPlayer);

        joinController.onGameStart( game -> {
            System.out.println("Starting game");
            Platform.runLater(() -> {
                primaryStage.setScene(gameScene);
            });
        });

        joinController.start();
    }

    public void startServer() throws IOException {
        GameJoiner joiner = new GameJoiner();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/games", new HttpObjectHandler(joiner::getGameRooms));
        server.createContext("/join", new HttpObjectHandler(joiner::handleJoinRoomRequest));
        server.createContext("/start_game", new HttpObjectHandler(joiner::handleStartGameRequest));
        server.createContext("/current_version", new HttpObjectHandler(joiner::getCurrentVersion));

        server.createContext("/ping", new HttpObjectHandler(obj -> 200));
        server.createContext("/client_jar", new HttpFileHandler("out/artifacts/Territory_jar/Territory.jar"));

        server.start();
        System.out.println("Started on port: " + PORT);
    }


    private void exitApplication(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
    }
}
