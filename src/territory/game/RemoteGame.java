package territory.game;

import territory.game.action.PlayerAction;
import territory.game.player.GUIPlayer;
import territory.game.player.RemotePlayer;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class RemoteGame implements Game {

    private GUIPlayer localPlayer;

    private Socket socket;

    public RemoteGame(GUIPlayer localPlayer){
        this.localPlayer = localPlayer;
    }

    @Override
    public void receiveAction(PlayerAction action) {

    }

    @Override
    public void start(){
        try {
            this.socket = new Socket(RemotePlayer.GAME_ADDRESS, RemotePlayer.GAME_PORT);
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't initialize server socket in Remote Game", e);
        }

        new Thread(this::listenForState).start();
    }

    private void listenForState(){
        try (Scanner scanner = new Scanner(socket.getInputStream())) {
            while(!scanner.hasNext()){
                String string = scanner.next();
                System.out.println(string);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
