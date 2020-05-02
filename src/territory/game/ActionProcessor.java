package territory.game;

import territory.game.action.*;
import territory.game.action.player.*;
import territory.game.action.tick.DealDamageAction;
import territory.game.action.tick.GiveGoldAction;
import territory.game.action.tick.GiveStoneAction;
import territory.game.action.tick.PlaceStoneAction;
import territory.game.construction.*;
import territory.game.info.IllegalActionInfo;
import territory.game.info.IllegalWallInfo;
import territory.game.info.InsufficientFundsInfo;
import territory.game.info.LostUnitInfo;
import territory.game.player.Player;
import territory.game.unit.Builder;
import territory.game.unit.Miner;
import territory.game.unit.Soldier;
import territory.game.unit.Unit;


import java.util.ArrayList;
import java.util.List;

public class ActionProcessor {
    //max distance units will spawn from their village
    private int unitSpawnJitter = 50;

    private LocalGame game;

    private GameState currentState;
    private Player player;
    private Inventory currentInventory;

    public ActionProcessor(LocalGame game){
        this.game = game;
    }

    public void processAction(GameAction action){
        currentState = game.getState();
        player = game.getPlayer(action.getColor());
        currentInventory = currentState.getPlayerInventory(player);

        //Player actions
        if(action instanceof CreateVillageAction){
            processCreateVillageAction((CreateVillageAction)action);
        }
        else if(action instanceof CreatePostAction){
            processCreatePostAction((CreatePostAction)action);
        }
        else if(action instanceof CreateWallAction){
            processCreateWallAction((CreateWallAction)action);
        }
        else if(action instanceof TrainUnitsAction){
            processTrainUnitsAction((TrainUnitsAction)action);
        }
        else if(action instanceof DirectUnitAction){
            processDirectUnitAction((DirectUnitAction)action);
        }

        //Tick actions
        else if(action instanceof GiveGoldAction){
            processGiveGoldAction((GiveGoldAction)action);
        }
        else if(action instanceof GiveStoneAction){
            processGiveStoneAction((GiveStoneAction) action);
        }
        else if(action instanceof PlaceStoneAction){
            processPlaceStoneAction((PlaceStoneAction)action);
        }
        else if(action instanceof DealDamageAction){
            processDealDamageAction((DealDamageAction)action);
        }

        //Unhandled action
        else {
            System.out.println("Unprocessed action " + action.getClass().getName());
        }
    }


    private void processCreateVillageAction(CreateVillageAction action){
        if(currentInventory.takeGold(Village.getGoldPrice())){
            Village village = new Village(player.getColor(), action.getX(), action.getY());
            currentInventory.addVillage(village);
        }
        else{
            player.sendInfo(new InsufficientFundsInfo());
        }
    }

    private void processCreatePostAction(CreatePostAction action){
        if(currentInventory.takeGold(Post.getGoldPrice())){
            Post post = new Post(player.getColor(), action.getX(), action.getY());
            currentInventory.addPost(post);
        }
        else{
            player.sendInfo(new InsufficientFundsInfo());
        }
    }

    private void processCreateWallAction(CreateWallAction action){

        if(currentInventory.getGold() < Wall.getGoldPrice()){
            player.sendInfo(new InsufficientFundsInfo());
            return;
        }

        Post post1 = currentInventory.getPost(action.getPost1Index());
        Post post2 = currentInventory.getPost(action.getPost2Index());

        //posts must be complete to build walls on
        if(!post1.isComplete() || !post2.isComplete()){
            player.sendInfo(IllegalWallInfo.POST_NOT_BUILT);
            return;
        }

        if(action.getPost1Index() == action.getPost2Index()){
            player.sendInfo(IllegalWallInfo.DUPLICATE_POST);
            return;
        }

        //if we get here, the wall is legal
        Wall wall = new Wall(player.getColor(), post1, post2);
        currentInventory.addWall(wall);
    }

    private void processTrainUnitsAction(TrainUnitsAction action){
        Village village = currentInventory.getVillage(action.getVillageIndex());

        int unitGoldPrice = unitGoldPrice(action);

        //determine how many of the requested units can actually be purchased
        int maxPurchase = currentInventory.getGold()/unitGoldPrice;

        //number limited by how much money we have and how much population there is
        int unitLimit = Math.min(maxPurchase, village.getPopulation());
        int numUnits = Math.min(action.getNumUnits(), unitLimit);

        if(unitLimit < action.getNumUnits()){
            player.sendInfo(new InsufficientFundsInfo());
        }

        currentInventory.takeGold(unitGoldPrice * numUnits);
        village.takePopulation(numUnits);

        List<Unit> newUnits = new ArrayList<>(numUnits);
        for(int i = 0; i < numUnits; i++){
            double x = village.getX() + RNG.randDouble(-unitSpawnJitter, unitSpawnJitter);
            double y = village.getY() + RNG.randDouble(-unitSpawnJitter, unitSpawnJitter);

            Unit newUnit = createNewUnit(action, x, y);

            newUnits.add(newUnit);
        }
        currentInventory.addUnits(newUnits);
    }

    /**
     * @param action the action which specifies unit types
     * @return the price in gold of each unit asked for by the given action
     */
    private int unitGoldPrice(TrainUnitsAction action){
        if(action instanceof TrainMinersAction){
            return Miner.getGoldPrice();
        }
        if(action instanceof TrainBuildersAction){
            return Builder.getGoldPrice();
        }
        if(action instanceof TrainSoldiersAction){
            return Soldier.getGoldPrice();
        }

        throw new RuntimeException("Unknown unit price");
    }

    /**
     * Create and initilize a single unit, according to what is asked for in the given action
     * @param action the action which specifies unit types
     * @return a new Unit according to the given action
     */
    private Unit createNewUnit(TrainUnitsAction action, double x, double y){
        if(action instanceof TrainMinersAction){
            Miner newMiner = new Miner(player, x, y);
            return newMiner;
        }

        if(action instanceof TrainBuildersAction){
            Builder newBuilder = new Builder(player, x, y);
            return newBuilder;
        }

        if(action instanceof TrainSoldiersAction){
            return new Soldier(player, x, y);
        }

        throw new RuntimeException("Unknown unit type to create");
    }

    private void processDirectUnitAction(DirectUnitAction action){
        Unit unit = currentInventory.getUnit(action.getUnitIndex());

        if(action instanceof DirectBuilderAction){
            processDirectBuilderAction((DirectBuilderAction)action);
        }
        else if(action instanceof DirectMinerAction){
            processDirectMinerAction((DirectMinerAction)action);
        }
        else if(action instanceof DirectSoldierAction){
            processDirectSoldierAction((DirectSoldierAction)action);
        }
        else{
            System.err.println("Unknown direct unit action");
        }
    }

    private void processDirectBuilderAction(DirectBuilderAction action){
        //unit must be a builder
        if(!(currentInventory.getUnit(action.getUnitIndex()) instanceof Builder)){
            player.sendInfo(new IllegalActionInfo("Direct builder but unit not builder"));
            return;
        }

        Builder builder = (Builder)currentInventory.getUnit(action.getUnitIndex());

        //find the project
        BuildProject project = null;
        BuildType projectType = action.getType();

        switch(projectType){
            case WALL:
                Wall wall = currentInventory.getWall(action.getTargetIndex());
                project = new BuildProject(wall);
                break;
            case POST:
                Post post = currentInventory.getPost(action.getTargetIndex());
                project = new BuildProject(post);
                break;
            default:
                System.err.println("Unknown build type in action processor");
        }

        builder.setProject(project);
    }

    private void processDirectMinerAction(DirectMinerAction action){
        Unit unit = currentInventory.getUnit(action.getUnitIndex());

        if( !(unit instanceof Miner)){
            player.sendInfo(new IllegalActionInfo("Direct miner but unit not miner"));
            return;
        }

        Miner miner = (Miner)unit;
        Mine mine = currentState.getMine(action.getTargetIndex());
        miner.setTarget(mine.getOpenMineSlot());
    }

    private void processDirectSoldierAction(DirectSoldierAction action){
        Unit unit = currentInventory.getUnit(action.getUnitIndex());

        if( !(unit instanceof Soldier)){
            player.sendInfo(new IllegalActionInfo("Direct soldier but unit not soldier"));
            return;
        }

        Soldier soldier = (Soldier)unit;
        Unit target = currentState.getPlayerInventory(game.getPlayer(action.getTargetColor()))
                        .getUnit(action.getTargetIndex());

        soldier.setTarget(target);
    }

    private void processGiveGoldAction(GiveGoldAction action) {
        currentInventory.giveGold(action.getCount());
    }

    private void processGiveStoneAction(GiveStoneAction action) {
        currentInventory.giveStone(action.getCount());
    }

    private void processPlaceStoneAction(PlaceStoneAction action){

        //stone that we can add is limited by how much stone we have
        int stoneToAdd = Math.min(currentInventory.getStone(), action.getStone());

        if(stoneToAdd < 1){
            return;
        }

        int stoneAdded = action.getBuildable().giveStone(action.getStone());

        currentInventory.takeStone(stoneAdded);
    }

    private void processDealDamageAction(DealDamageAction action){
        Unit unit = action.getTarget();
        unit.dealDamage(action.getDamage());

        if(unit.getHealth() <= 0) {
            unit.kill();

            //update player's inventory to reflect unit killed
            Player unitOwner = game.getPlayer(unit.getColor());
            Inventory inventory = currentState.getPlayerInventory(unitOwner);
            inventory.removeUnit(unit);

            unitOwner.sendInfo(new LostUnitInfo(unit.getIndex()));
        }
    }
}
