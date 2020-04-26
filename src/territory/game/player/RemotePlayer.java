package territory.game.player;

import territory.game.GameState;
import territory.game.info.GameInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RemotePlayer extends Player {

    public static final String GAME_ADDRESS = "localhost";
    public static final int GAME_PORT = 3400;

    private Socket socket;

    private PrintWriter writer;

    public RemotePlayer(Socket socket){
        this.socket = socket;
        try {
            this.writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to open printwriter in Remote Player", e);
        }
    }


    @Override
    protected void receiveState(GameState state) {
        System.out.println("Sending state.");
        writer.println("Here's a state");
        System.out.println("Sent the state.");
    }

    @Override
    protected void receiveInfo(GameInfo info) {

    }
}
