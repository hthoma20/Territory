package game.player;

import game.GameState;
import game.LocalGame;
import game.info.GameInfo;
import game.info.InsufficientFundsInfo;
import game.unit.Miner;
import gui.Controller;

public class GUIPlayer extends Player {
    Controller controller;

    public GUIPlayer(Controller controller){
        this.controller = controller;
        controller.setPlayer(this);
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
