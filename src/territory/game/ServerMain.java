package territory.game;

import territory.game.LocalGame;
import territory.game.player.RemotePlayer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain extends Application {

    public static final String HOST = "localhost";
    public static final int PORT = 1014;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        RemotePlayer player = new RemotePlayer(PORT);

        System.out.println("Connecting...");
        player.connect();
        System.out.println("Connected.");

        LocalGame game = new LocalGame(player);

        game.start();
    }
}
