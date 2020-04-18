package game.player;

import game.*;
import game.action.GameAction;
import game.action.PlayerAction;
import game.info.GameInfo;

public abstract class Player implements Indexable {
    private LocalGame game;

    protected int index = -1;

    protected GameColor color;


    public void setGame(LocalGame game){
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
        new Thread(() -> this.receiveInfo(info)).start();
    }

    protected abstract void receiveState(GameState state);

    protected abstract void receiveInfo(GameInfo info);

    public void takeAction(PlayerAction action){
        this.game.receiveAction(action);
    }
}
