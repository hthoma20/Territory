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

        RemotePlayer player1 = new RemotePlayer(PORT);

        System.out.println("Connecting 1...");
        player1.connect();
        System.out.println("Connected 1.");

        RemotePlayer player2 = new RemotePlayer(PORT+1);
        System.out.println("Connecting 2...");
        player2.connect();
        System.out.println("Connected 2.");

        LocalGame game = new LocalGame(player1, player2);

        game.start();
    }
}
