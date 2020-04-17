package game;

import game.action.CreateVillageAction;
import game.action.GameAction;
import game.construction.Village;
import game.info.InsufficientFundsInfo;

import javax.swing.*;

public class ActionProcessor {
    private LocalGame game;

    public ActionProcessor(LocalGame game){
        this.game = game;
    }

    public void processAction(GameAction action){
        if(action instanceof CreateVillageAction){
            processCreateVillageAction((CreateVillageAction)action);
        }
    }

    public void processCreateVillageAction(CreateVillageAction action){
        Inventory inventory = action.getPlayer().getInventory();

        if(inventory.takeGold(Village.getGoldPrice())){
            Village village = new Village(action.getPlayer(), action.getX(), action.getY());
            action.getPlayer().getInventory().addVillage(village);
        }
        else{
            action.getPlayer().sendInfo(new InsufficientFundsInfo());
        }
    }
}
