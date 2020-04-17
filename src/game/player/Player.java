package game.player;

import game.GameColor;
import game.GameState;
import game.Inventory;
import game.LocalGame;
import game.action.GameAction;
import game.info.GameInfo;

public abstract class Player {
    private LocalGame game;

    private int index = -1;

    protected GameColor color;

    protected Inventory inventory = new Inventory();

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

    public Inventory getInventory(){
        return this.inventory;
    }

    public void sendState(GameState state){
        new Thread(() -> this.receiveState(state));
    }

    public void sendInfo(GameInfo info){
        new Thread(() -> this.receiveInfo(info));
    }

    protected abstract void receiveState(GameState state);

    protected abstract void receiveInfo(GameInfo info);

    public void takeAction(GameAction action){
        this.game.receiveAction(action);
    }
}
