package game.player;

import game.GameState;
import game.LocalGame;
import game.action.CreatePostAction;
import game.action.CreateVillageAction;
import game.action.TrainBuildersAction;
import game.action.TrainMinersAction;
import game.info.GameInfo;
import game.info.InsufficientFundsInfo;
import game.unit.Miner;
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

        takeAction(new CreateVillageAction(this, 100, 50));
        takeAction(new TrainMinersAction(this, 0, 1));
        takeAction(new CreatePostAction(this, -100, -50));
        takeAction(new TrainBuildersAction(this, 0, 1));
    }

    @Override
    public void receiveState(GameState state) {
        controller.updateDisplay(state);
    }

    @Override
    public void receiveInfo(GameInfo info){
        if(info instanceof InsufficientFundsInfo){
            System.out.println("Insufficient funds");
        }
        else{
            System.out.println("Unhandled info");
        }
    }
}
