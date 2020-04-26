package territory;

import territory.game.LocalGame;
import territory.game.player.RemotePlayer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SeverMain extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Socket socket = null;

        try {
            ServerSocket serverSocket = new ServerSocket(RemotePlayer.GAME_PORT);
            System.out.println("Listening...");
            socket = serverSocket.accept();
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        RemotePlayer player = new RemotePlayer(socket);

        LocalGame game = new LocalGame(player);

        game.start();
    }
}
