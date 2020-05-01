package territory.joiner;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class JoinerMain {

    public static final int PORT = 4000;

    public static void main(String[] args) throws IOException {
        GameJoiner joiner = new GameJoiner();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/games", new GetObjectHttpHandler(joiner::handleGetGameRequest));
        server.start();
        System.out.println("Started on port: " + PORT);
    }
}
