package game;

import game.action.CreateVillageAction;
import game.action.GameAction;
import game.action.TrainMinersAction;
import game.construction.Village;
import game.info.InsufficientFundsInfo;
import game.player.Player;
import game.unit.Miner;
import game.unit.Unit;


import java.util.ArrayList;
import java.util.List;

public class ActionProcessor {
    //max distance units will spawn from their village
    private int unitSpawnJitter = 7;

    private LocalGame game;

    private GameState currentState;
    private Player player;
    private Inventory currentInventory;

    public ActionProcessor(LocalGame game){
        this.game = game;
    }

    public void processAction(GameAction action){
        currentState = game.getState();
        player = action.getPlayer();
        currentInventory = currentState.getPlayerInventory(player);

        if(action instanceof CreateVillageAction){
            processCreateVillageAction((CreateVillageAction)action);
        }
        else if(action instanceof TrainMinersAction){
            processTrainMinersAction((TrainMinersAction)action);
        }
        else{
            System.out.println("Unprocessed action " + action.getClass().getName());
        }
    }

    private void processCreateVillageAction(CreateVillageAction action){
        if(currentInventory.takeGold(Village.getGoldPrice())){
            Village village = new Village(player, action.getX(), action.getY());
            currentInventory.addVillage(village);
        }
        else{
            player.sendInfo(new InsufficientFundsInfo());
        }
    }

    private void processTrainMinersAction(TrainMinersAction action){
        Village village = currentInventory.getVillage(action.getVillageIndex());

        //determine how many of the requested miners can actually be purchased
        int maxPurchase = currentInventory.getGold()/ Miner.getGoldPrice();

        //number limited by how much money we have and how much population there is
        int minerLimit = Math.min(maxPurchase, village.getPopulation());
        int numMiners = Math.min(action.getNumUnits(), minerLimit);

        if(minerLimit < action.getNumUnits()){
            player.sendInfo(new InsufficientFundsInfo());
        }

        currentInventory.takeGold(Miner.getGoldPrice() * numMiners);
        village.takePopulation(numMiners);

        List<Unit> newMiners = new ArrayList<>(numMiners);
        for(int i = 0; i < numMiners; i++){
            double x = village.getX() + RNG.randDouble(-unitSpawnJitter, unitSpawnJitter);
            double y = village.getY() + RNG.randDouble(-unitSpawnJitter, unitSpawnJitter);

            Miner newMiner = new Miner(player, x, y);
            newMiner.setTarget(currentState.getMine(0));

            newMiners.add(newMiner);
        }
        currentInventory.addUnits(newMiners);
    }
}
