package territory.game;

import territory.game.action.player.PlayerAction;
import territory.game.info.GameInfo;
import territory.game.info.JoinInfo;
import territory.game.player.GUIPlayer;
import territory.joiner.JoinInfoListener;

public class RemoteGame implements Game {

    private GUIPlayer localPlayer;

    private SocketConnection connection;

    private JoinInfoListener joinInfoListener;

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

            if(object instanceof JoinInfo && joinInfoListener != null){
                joinInfoListener.receiveJoinInfo((JoinInfo) object);
            }
        }
        else{
            System.err.println("Unexpected object received");
        }
    }

    public void setJoinInfoListener(JoinInfoListener joinInfoListener){
        this.joinInfoListener = joinInfoListener;
    }

    @Override
    public void receiveAction(PlayerAction action) {
        connection.sendMessage(action);
    }

    @Override
    public void start(){
        System.out.println("Remote game -- nothing to start.");
    }

    public void connect(){
        boolean connected = connection.connect();

        if(!connected){
            throw new GameNotStartedException("Couldn't establish connection to remote game");
        }
    }
}
