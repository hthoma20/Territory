package territory.game;

import javafx.geometry.Point2D;
import territory.game.action.*;
import territory.game.action.player.*;
import territory.game.action.tick.*;
import territory.game.construction.*;
import territory.game.construction.upgrade.VillageUpgrade;
import territory.game.info.*;
import territory.game.player.Player;
import territory.game.target.BuildProject;
import territory.game.target.BuildType;
import territory.game.target.PatrolArea;
import territory.game.unit.*;


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
        else if(action instanceof DirectSoldiersAction){
            processDirectSoldiersAction((DirectSoldiersAction)action);
        }
        else if(action instanceof UpgradeVillageAction){
            processUpgradeVillageAction((UpgradeVillageAction) action);
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
        else if(action instanceof CheckTerritoryAction){
            processCheckTerritoryAction((CheckTerritoryAction)action);
        }
        else if(action instanceof FellTreeAction){
            processFellTreeAction((FellTreeAction) action);
        }

        //Unhandled action
        else {
            System.out.println("Unprocessed action " + action.getClass().getName());
        }
    }

    private void processCreateVillageAction(CreateVillageAction action){
        int goldPrice = Post.getGoldPrice();

        if(currentInventory.getGold() < goldPrice){
            player.sendInfo(new InsufficientFundsInfo());
            return;
        }

        if(!isValidConstruction(action.getX(), action.getY())) {
            player.sendInfo(new IllegalConstructionInfo());
            return;
        }

        currentInventory.takeGold(Village.getGoldPrice());
        Village village = new Village(player.getColor(), action.getX(), action.getY());
        currentInventory.addVillage(village);

    }

    private void processCreatePostAction(CreatePostAction action){
        int goldPrice = Post.getGoldPrice();

        if(currentInventory.getGold() < goldPrice){
            player.sendInfo(new InsufficientFundsInfo());
            return;
        }

        if(!isValidConstruction(action.getX(), action.getY())) {
            player.sendInfo(new IllegalConstructionInfo());
            return;
        }

        currentInventory.takeGold(goldPrice);
        Post post = new Post(player.getColor(), action.getX(), action.getY());
        currentInventory.addPost(post);
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

        //cannot build wall on single post
        if(action.getPost1Index() == action.getPost2Index()){
            player.sendInfo(IllegalWallInfo.DUPLICATE_POST);
            return;
        }

        //cannot build wall intersecting current wall
        for(Wall existingWall : currentInventory.getWalls()){

            boolean post1Matches = existingWall.containsPost(post1);
            boolean post2Matches = existingWall.containsPost(post2);

            //if this wall matches the new wall
            if(post1Matches && post2Matches){
                player.sendInfo(IllegalWallInfo.DUPLICATE_WALL);
                return;
            }

            //if this wall intersects the new wall
            if(!post1Matches && !post2Matches && existingWall.intersects(post1, post2)){
                player.sendInfo(IllegalWallInfo.INTERSECTION);
                return;
            }
        }

        //if we get here, the wall is legal
        Wall wall = new Wall(player.getColor(), post1, post2);
        currentInventory.addWall(wall);
    }

    /**
     * @param x the x-coord of the new construction
     * @param y the y-coord of the new construction
     * @return whether it is legal to build a new construction at the given coordinate
     */
    private boolean isValidConstruction(double x, double y){
        Point2D point = new Point2D(x, y);

        //check that it is in bounds
        if(!currentState.getAreaInPlay().contains(x, y)){
            return false;
        }

        //check if it is too close to another construction
        for(Construction construction : currentState.getAllConstructions()){
            //if the point is too close to the construction
            if(point.distance(construction.getX(), construction.getY()) < construction.getBuildZoneRadius()){
                return false;
            }
        }

        return true;
    }

    private void processTrainUnitsAction(TrainUnitsAction action){
        Village village = currentInventory.getVillage(action.getVillageIndex());

        //can these units be trained from this village?
        if(!village.canTrain(action.getUnitClass())){
            player.sendInfo(new IllegalTrainUnitInfo(action.getUnitClass()));
            return;
        }

        int unitGoldPrice = unitGoldPrice(action);

        //determine how many of the requested units can actually be purchased
        int maxPurchase = currentInventory.getGold()/unitGoldPrice;

        //number limited by how much money we have and how much population there is
        int unitLimit = Math.min(maxPurchase, village.getPopulation());
        int numUnits = Math.min(action.getNumUnits(), unitLimit);

        if(unitLimit < action.getNumUnits()){
            player.sendInfo(new InsufficientFundsInfo());
        }

        village.canTrain(Soldier.class);

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
        if(action instanceof TrainLumberjacksAction){
            return Lumberjack.getGoldPrice();
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
            Soldier soldier = new Soldier(player, x, y);
            soldier.setPatrolArea(new PatrolArea(soldier.getColor(), x, y, 50));
            return soldier;
        }

        if(action instanceof TrainLumberjacksAction){
            return new Lumberjack(player, x, y);
        }

        throw new RuntimeException("Unknown unit type to create");
    }

    private void processDirectUnitAction(DirectUnitAction action){
        if(action.getUnitIndex() >= currentInventory.getUnits().size()){
            player.sendInfo(new IllegalActionInfo("Direct unit index out of bounds"));
            return;
        }

        if(action instanceof DirectBuilderAction){
            processDirectBuilderAction((DirectBuilderAction)action);
        }
        else if(action instanceof DirectMinerAction){
            processDirectMinerAction((DirectMinerAction)action);
        }
        else if(action instanceof DirectSoldierAction){
            processDirectSoldierAction((DirectSoldierAction)action);
        }
        else if(action instanceof DirectLumberjackAction){
            processDirectLumberjackAction((DirectLumberjackAction) action);
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

    private void processDirectLumberjackAction(DirectLumberjackAction action){
        Unit unit = currentInventory.getUnit(action.getUnitIndex());

        if( !(unit instanceof Lumberjack)){
            player.sendInfo(new IllegalActionInfo("Direct lumberjack but unit not lumberjack"));
            return;
        }

        Lumberjack jack = (Lumberjack)unit;
        Tree tree = currentState.getTree(action.getTargetIndex());
        jack.setTarget(tree);
    }

    private void processDirectSoldierAction(DirectSoldierAction action){
        Unit unit = currentInventory.getUnit(action.getUnitIndex());

        if( !(unit instanceof Soldier)){
            player.sendInfo(new IllegalActionInfo("Direct soldier but unit not soldier"));
            return;
        }

        Soldier soldier = (Soldier)unit;
        soldier.setPatrolArea(action.getPatrolArea());
    }

    private void processDirectSoldiersAction(DirectSoldiersAction action){
        int maxIndex = currentInventory.getUnits().size();

        for(int index : action.getIndices()){
            if(index < maxIndex) {
                processDirectSoldierAction(
                        new DirectSoldierAction(action.getColor(), index, action.getPatrolArea()));
            }
        }
    }

    private void processUpgradeVillageAction(UpgradeVillageAction action){
        int woodPrice = action.getUpgrade().getWoodPrice();

        if(woodPrice > currentInventory.getWood()){
            player.sendInfo(new InsufficientFundsInfo());
            return;
        }

        Village village = currentInventory.getVillage(action.getVillageIndex());
        currentInventory.takeWood(woodPrice);
        village.upgrade(action.getUpgrade());
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

    private void processCheckTerritoryAction(CheckTerritoryAction action){
        TerritoryList territories = Territory.fromWalls(currentInventory.getWalls());
        currentState.setPlayerTerritories(player.getIndex(), territories);
    }

    private void processFellTreeAction(FellTreeAction action){
        Tree tree = action.getFelledTree();
        currentInventory.giveWood(tree.getWood());
    }
}
