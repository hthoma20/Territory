package territory.game.player;

import territory.game.*;
import territory.game.action.player.PlayerAction;
import territory.game.info.GameInfo;
import territory.game.info.PlayerSetupInfo;

import java.io.Serializable;

public abstract class Player implements Indexable, Serializable {
    private Game game;

    protected int index = -1;

    protected GameColor color;


    public void setGame(Game game){
        this.game = game;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public void setColor(GameColor color){
        this.color = color;
    }

    public GameColor getColor(){
        return this.color;
    }

    public int getIndex() {
        return index;
    }

    public void sendState(GameState state){
        new Thread(() -> this.receiveState(state)).start();
    }

    public void sendInfo(GameInfo info){
        if(info instanceof PlayerSetupInfo){
            this.index = ((PlayerSetupInfo) info).getIndex();
            this.color = ((PlayerSetupInfo) info).getColor();
        }

        new Thread(() -> this.receiveInfo(info)).start();
    }

    protected abstract void receiveState(GameState state);

    protected abstract void receiveInfo(GameInfo info);

    public void takeAction(PlayerAction action){
        this.game.receiveAction(action);
    }
}
