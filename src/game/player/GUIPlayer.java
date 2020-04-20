package game.player;

import game.GameState;
import game.LocalGame;
import game.action.*;
import game.info.GameInfo;
import gui.Controller;

public class GUIPlayer extends Player {
    private Controller controller;

    public GUIPlayer(Controller controller){
        this.controller = controller;
        controller.setPlayer(this);
    }

    /**
     * Take initial actions
     */
    @Override
    public void setGame(LocalGame game){
        super.setGame(game);


        takeAction(new CreatePostAction(this, -100, -50));
        takeAction(new CreatePostAction(this, 100, -50));
        takeAction(new CreateVillageAction(this, 100, 100));
    }

    @Override
    public void receiveState(GameState state) {
        controller.updateDisplay(state);
    }

    @Override
    public void receiveInfo(GameInfo info){
        System.out.println(info);
    }
}
