package territory;

import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.stage.Stage;
import territory.joiner.GameJoiner;
import territory.joiner.handler.HttpFileHandler;
import territory.joiner.handler.HttpObjectHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerMain extends Application {

    public static final String HOST = "localhost";
    public static final int PORT = 4000;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        startServer();
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
}
