package territory.game;

import territory.game.action.player.PlayerAction;
import territory.game.info.GameInfo;
import territory.game.player.GUIPlayer;

public class RemoteGame implements Game {

    private GUIPlayer localPlayer;

    private SocketConnection connection;

    public RemoteGame(GUIPlayer localPlayer, String host, int port){
        this.localPlayer = localPlayer;
        this.connection = SocketConnection.createClient(host, port, this::receiveObjectFromServer);

        localPlayer.setGame(this);
    }

    private void receiveObjectFromServer(Object object){
        if(object instanceof GameState){
            localPlayer.sendState((GameState) object);
        }
        else if(object instanceof GameInfo){
            localPlayer.sendInfo((GameInfo) object);
        }
        else{
            System.err.println("Unexpected object received");
        }
    }

    @Override
    public void receiveAction(PlayerAction action) {
        connection.sendMessage(action);
    }

    @Override
    public void start(){
        boolean connected = connection.connect();

        if(!connected){
            throw new GameNotStartedException("Couldn't establish connection to remote game");
        }
    }
}
