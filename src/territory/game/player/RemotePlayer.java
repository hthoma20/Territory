package territory.game.player;

import territory.game.GameState;
import territory.game.SocketConnection;
import territory.game.action.player.PlayerAction;
import territory.game.info.GameInfo;
import territory.game.info.NameInfo;


public class RemotePlayer extends Player {


    private SocketConnection connection;

    public RemotePlayer(int port, String name){
        super(name);
        this.connection = SocketConnection.createServer(port, this::receiveObjectFromClient);
    }

    public boolean connect(){
        boolean connected = connection.connect();

        if(connected){
            connection.sendMessage(new NameInfo(this.getName()));
        }

        return connected;
    }

    private void receiveObjectFromClient(Object object){
        if(object instanceof PlayerAction){
            takeAction((PlayerAction) object);
        }
        else{
            System.err.println("Unexpected object received from client");
        }
    }

    @Override
    protected void receiveState(GameState state) {
        connection.sendMessage(state);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        connection.sendMessage(info);
    }
}
