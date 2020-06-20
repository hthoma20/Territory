package territory.gui;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import territory.game.*;
import territory.game.action.player.*;
import territory.game.construction.*;
import territory.game.construction.upgrade.WorkShopItem;
import territory.game.construction.upgrade.VillageUpgrade;
import territory.game.construction.upgrade.WorkShop;
import territory.game.player.GUIPlayer;
import territory.game.sprite.ImageStore;
import territory.game.sprite.Sprite;
import territory.game.target.BuildType;
import territory.game.target.PatrolArea;
import territory.game.unit.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import territory.gui.component.SwapPane;
import territory.gui.input.InputProcessor;
import territory.gui.input.MouseDragInput;
import territory.gui.input.MouseInput;
import territory.gui.input.MouseScrollInput;
import territory.util.StateReceivedListener;
import territory.util.StringUtils;

import javax.swing.plaf.nimbus.State;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Controller {

    @FXML private Pane canvasPane;
    @FXML private Canvas canvas;

    @FXML private Node rightClickMenu;

    @FXML private Label stoneLabel;
    @FXML private Label goldLabel;
    @FXML private Label woodLabel;
    @FXML private Label territoryLabel;
    @FXML private Label minersLabel;
    @FXML private Label lumberjacksLabel;
    @FXML private Label buildersLabel;
    @FXML private Label soldiersLabel;
    @FXML private Label villagesLabel;
    @FXML private Label populationLabel;

    private Map<WorkShopItem, Label> itemStockLabels;

    //info pane
    @FXML private SwapPane infoSwapPane;
    @FXML private Pane villagePane;
    @FXML private Pane villageUpgradesPane;
    @FXML private Pane workShopPane;
    @FXML private Pane shopUpgradesPane;
    @FXML private Pane benchPane;

    //labels for prices of things on the GUI
    @FXML private Label minerPriceLabel;
    @FXML private Label builderPriceLabel;
    @FXML private Label soldierPriceLabel;
    @FXML private Label lumberjackPriceLabel;
    @FXML private Label villagePriceLabel;
    @FXML private Label postPriceLabel;

    //color-specific imageviews on the gui
    @FXML private ImageView villageImageView;
    @FXML private ImageView postImageView;
    @FXML private ImageView minerImageView;
    @FXML private ImageView lumberjackImageView;
    @FXML private ImageView builderImageView;
    @FXML private ImageView soldierImageView;

    //cursors
    private Map<InteractMode, Cursor> cursorMap;

    private Scene scene;

    private CanvasPainter canvasPainter;

    private GameState currentState;
    private Inventory currentInventory;

    private InteractMode currentInteractMode;

    private Selection currentSelection = new Selection();

    private PatrolArea patrolArea;
    private RectangleArea selectionArea;

    private GUIPlayer player;

    //map from predicate to function
    //the function will be called the first time that a state matching the predicate is received
    private final Map<Predicate<GameState>, StateReceivedListener> statePredicates = new HashMap<>();

    public void init(Scene scene){
        this.scene = scene;

        //resize the canvas with its pane
        this.canvas.widthProperty().bind(canvasPane.widthProperty());
        this.canvas.heightProperty().bind(canvasPane.heightProperty());


        this.canvasPainter = new CanvasPainter(this, canvas);

        this.currentInteractMode = InteractMode.CREATE_VILLAGE;

        infoSwapPane.show(villagePane);

        initPriceLabels();
        initStockLabels();

        InputProcessor inputProcessor = new InputProcessor(scene, canvas);

        inputProcessor.setOnScroll(this::handleCanvasScroll);
        //inputProcessor.setOnMiddleDrag(this::handleMiddleDrag);
        inputProcessor.setOnRightDrag(this::handleRightDrag);
        inputProcessor.setOnLeftDrag(this::handleLeftDrag);
        inputProcessor.setOnLeftRelease(this::handleLeftReleased);
        inputProcessor.setOnRightRelease(this::handleRightReleased);
        inputProcessor.setOnLeftClick(this::handleLeftClick);
        inputProcessor.setOnRightClick(this::handleRightClick);
        inputProcessor.setOnMouseMove(this::handleMouseMoved);

        canvas.setOnMouseExited(event -> scene.setCursor(null));
    }

    public void setPlayer(GUIPlayer player){
        this.player = player;
    }

    /**
     * Initialize color-specific images
     * @param color the color of the player
     */
    public void initImages(GameColor color){
        this.villageImageView.setImage(ImageStore.store.imageFor(Village.class, color));
        this.postImageView.setImage(ImageStore.store.imageFor(Post.class, color));
        this.minerImageView.setImage(ImageStore.store.imageFor(Miner.class, color));
        this.lumberjackImageView.setImage(ImageStore.store.imageFor(Lumberjack.class, color));
        this.builderImageView.setImage(ImageStore.store.imageFor(Builder.class, color));
        this.soldierImageView.setImage(ImageStore.store.imageFor(Soldier.class, color));

        initCursors(color);
    }

    private void initCursors(GameColor color){
        this.cursorMap = new HashMap<>(4);

        cursorMap.put(InteractMode.CREATE_VILLAGE, cursorForSprite(Village.class, color));
        cursorMap.put(InteractMode.CREATE_POST, cursorForSprite(Post.class, color));
        cursorMap.put(InteractMode.DIRECT_SOLDIER, Cursor.CROSSHAIR);
        cursorMap.put(InteractMode.NONE, Cursor.DEFAULT);
    }

    private Cursor cursorForSprite(Class<? extends Sprite> spriteClass, GameColor color){
        Image image = ImageStore.store.imageFor(spriteClass, color);
        return new ImageCursor(image, image.getWidth()/2, image.getHeight()/2);
    }

    //Parse the user data from the given event as an int,
    //throw an exception if that is not possible
    private int userDataInt(Event e){
        return Integer.parseInt(userDataString(e));
    }

    //Parse the user data from the given event as a String,
    //throw an exception if that is not possible
    private String userDataString(Event e){
        Node targetNode = (Node)e.getTarget();
        Object userData = targetNode.getUserData();

        return (String)userData;
    }

    public void gameOver(GameColor winner){
        canvasPainter.gameOver(winner);
    }

    public void updateDisplay(GameState state){
        currentState = state;
        currentInventory = state.getPlayerInventory(player);

        Platform.runLater(()-> {

            //call predicate functions
            runStatePredicates();

            canvasPainter.fitToArea(state.getAreaInPlay());
            //paint the canvas
            canvasPainter.paint();

            updateLabels();
        });
    }

    private void runStatePredicates(){
        List<Predicate<GameState>> truePredicates = new ArrayList<>();
        synchronized (statePredicates){

            //check whether each predicate is satisfied
            for(Predicate<GameState> predicate : statePredicates.keySet()){
                if(predicate.test(currentState)){
                    //if it is mark it so
                    truePredicates.add(predicate);
                }
            }
        }

        //run the corresponding functions
        for(Predicate<GameState> truePredicate : truePredicates){
            statePredicates.get(truePredicate).run();
        }

        statePredicates.keySet().removeAll(truePredicates);
    }

    private void updateLabels(){
        //update the labels
        TerritoryList territories = currentState.getPlayerTerritories(this.player.getIndex());
        stoneLabel.setText(""+currentInventory.getStone());
        goldLabel.setText(""+currentInventory.getGold());
        woodLabel.setText(""+currentInventory.getWood());
        territoryLabel.setText(String.format("%,d", (int)(territories.area()/1000)));

        updateCountLabels();

        updateShopLabels();
    }

    private void updateCountLabels(){
        int minerCount = 0;
        int lumberjackCount = 0;
        int builderCount = 0;
        int soldierCount = 0;

        for(Unit unit : currentInventory.getUnits()){
            if(unit instanceof Miner){
                minerCount++;
            }
            else if(unit instanceof Lumberjack){
                lumberjackCount++;
            }
            else if(unit instanceof Builder){
                builderCount++;
            }
            else if(unit instanceof Soldier){
                soldierCount++;
            }
            else{
                System.err.println(
                        "Unknown unit type in Controller.updateUnitCountLabels: " + unit.getClass().getSimpleName());
            }
        }

        minersLabel.setText("" + minerCount);
        lumberjacksLabel.setText("" + lumberjackCount);
        buildersLabel.setText("" + builderCount);
        soldiersLabel.setText("" + soldierCount);

        int villageCount = currentInventory.getVillages().size();
        villagesLabel.setText("" + villageCount);

        int population = 0;
        for(Village village : currentInventory.getVillages()){
            population += village.getPopulation();
        }
        populationLabel.setText("" + population);
    }

    private void updateShopLabels(){
        if(currentSelection.getType() != Selection.Type.VILLAGE){
            return;
        }

        Village village = currentInventory.getVillage(currentSelection.getIndex());
        WorkShop shop = village.getWorkShop();

        if(shop == null){
            return;
        }

        for(WorkShopItem item : WorkShopItem.values()){
            itemStockLabels.get(item).setText(""+shop.stock(item));
        }
    }

    private void initPriceLabels(){
        int minerGold = Miner.getGoldPrice();
        int lumberjackGold = Lumberjack.getGoldPrice();
        int builderGold = Builder.getGoldPrice();
        int soldierGold = Soldier.getGoldPrice();
        int villageGold = Village.getGoldPrice();
        int postGold = Post.getGoldPrice();

        minerPriceLabel.setText("" + minerGold);
        lumberjackPriceLabel.setText("" + lumberjackGold);
        builderPriceLabel.setText("" + builderGold);
        soldierPriceLabel.setText("" + soldierGold);

        villagePriceLabel.setText("" + villageGold);
        postPriceLabel.setText("" + postGold);
    }

    private void initStockLabels(){
        WorkShopItem[] items = WorkShopItem.values();

        this.itemStockLabels = new HashMap<>(items.length);

        for(WorkShopItem item : items){
            itemStockLabels.put(item, new Label("No data"));
        }
    }

    @FXML
    public void trainUnitsButtonClicked(ActionEvent actionEvent){

        //we cannot train units from no village
        if(currentSelection.getType() != Selection.Type.VILLAGE){
            return;
        }

        //user data should have the form "<unit type>:<number>", i.e. "Soldier:10"
        String arg = userDataString(actionEvent);
        String[] parsedArg = arg.split(":");
        String unitType = parsedArg[0].toLowerCase();
        int count = Integer.parseInt(parsedArg[1]);

        switch(unitType){
            case "miner":
                player.takeAction(new TrainMinersAction(this.player.getColor(), currentSelection.getIndex(), count));
                return;
            case "builder":
                player.takeAction(new TrainBuildersAction(this.player.getColor(), currentSelection.getIndex(), count));
                return;
            case "soldier":
                player.takeAction(new TrainSoldiersAction(this.player.getColor(), currentSelection.getIndex(), count));
                return;
            case "lumberjack":
                player.takeAction(new TrainLumberjacksAction(this.player.getColor(), currentSelection.getIndex(), count));
                return;
        }
    }

    @FXML
    public void interactModeButtonClicked(ActionEvent actionEvent){
        String mode = userDataString(actionEvent);
        this.currentInteractMode = InteractMode.valueOf(mode);

        scene.setCursor(cursorMap.get(currentInteractMode));

        System.out.println("Setting interaction mode to " + currentInteractMode);
    }

    private void handleCanvasScroll(MouseScrollInput scrollInput){
        //either up one or down one
        int delta = scrollInput.getDeltaY() > 0 ? 1 : -1;

        InteractMode[] modes = InteractMode.values();

        int newMode = Math.floorMod(currentInteractMode.ordinal() + delta, modes.length);

        //if we can't actually select soldiers
        if(modes[newMode] == InteractMode.DIRECT_SOLDIER && !soldierSelected()){
            newMode = Math.floorMod(newMode + delta, modes.length);
        }

        currentInteractMode = modes[newMode];

        scene.setCursor(cursorMap.get(currentInteractMode));
    }

    private void handleMiddleDrag(MouseDragInput dragInput){
        //pan the canvas
        canvasPainter.drag(-dragInput.getDeltaX(), -dragInput.getDeltaY());
    }

    private void handleMouseMoved(MouseInput input){
        Point2D gamePoint = canvasPainter.canvasPointToGamePoint(input.getX(), input.getY());

        //if it's off the map
        if(!currentState.getAreaInPlay().contains(gamePoint.getX(), gamePoint.getY())){
            scene.setCursor(null);
            return;
        }

        List<Sprite> spritesHovered = currentState.getSpritesContaining(gamePoint);

        //if we aren't about to click something
        if(spritesHovered.isEmpty()){
            scene.setCursor(cursorMap.get(currentInteractMode));
        }
        //if we are about to click something
        else{
            scene.setCursor(Cursor.HAND);
        }
    }

    private void handleLeftDrag(MouseDragInput dragInput){
        //update patrol area to direct soldiers to
        if(currentInteractMode != InteractMode.DIRECT_SOLDIER){
            return;
        }

        Point2D currPoint = canvasPainter.canvasPointToGamePoint(dragInput.getX(), dragInput.getY());
        Point2D patrolCenter = canvasPainter.canvasPointToGamePoint(dragInput.getStartX(), dragInput.getStartY());

        this.patrolArea =
                new PatrolArea(player.getColor(), patrolCenter.getX(), patrolCenter.getY(),
                        patrolCenter.distance(currPoint));
    }

    private void handleLeftReleased(MouseInput mouseInput){
        //direct soldiers
        if(currentInteractMode != InteractMode.DIRECT_SOLDIER){
            return;
        }

        //this means that the spot was clicked as opposed to a circle created
        if(this.patrolArea == null){
            return;
        }

        directSoldiersTo(this.patrolArea);
        currentSelection.clear();
        this.patrolArea = null;
    }

    private void handleRightDrag(MouseDragInput dragInput){
        //update the selection area
        Point2D p1 = canvasPainter.canvasPointToGamePoint(dragInput.getStartX(), dragInput.getStartY());
        Point2D p2 = canvasPainter.canvasPointToGamePoint(dragInput.getX(), dragInput.getY());

        this.selectionArea = new RectangleArea(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    private void handleRightReleased(MouseInput mouseInput){

        //if there is no selection box, clear the selection
        if(selectionArea == null){
            currentSelection.clear();
            return;
        }

        //select all units in selected rectangle
        for(Unit unit : currentState.getAllUnitsInArea(selectionArea)){
            if(unit.getColor() != player.getColor()){
                continue;
            }

            selectUnit(unit);
        }

        selectionArea = null;
    }

    private void handleLeftClick(MouseInput input){

        if(rightClickMenu.isVisible()){
            rightClickMenu.setVisible(false);
            return;
        }

        //find the point that was clicked in the territory game
        Point2D gamePoint = canvasPainter.canvasPointToGamePoint(input.getX(), input.getY());

        //first check if something was clicked
        List<Sprite> clickedSprites = currentState.getSpritesContaining(gamePoint);

        if(clickedSprites.size() > 0){
            spritesClicked(clickedSprites);
            return;
        }

        //otherwise, this is some type of interaction
        if(currentInteractMode == null){
            return;
        }

        PlayerAction action = null;

        switch (currentInteractMode) {
            case CREATE_VILLAGE:
                System.out.println("Place Village");
                action = new CreateVillageAction(this.player.getColor(), gamePoint.getX(), gamePoint.getY());
                break;
            case CREATE_POST:
                System.out.println("Place Post");
                action = new CreatePostAction(this.player.getColor(), gamePoint.getX(), gamePoint.getY());
                break;
            default:
                System.out.println(String.format("Unhandled interact mode %s", currentInteractMode));
                break;
        }

        if(action != null){
            this.player.takeAction(action);
        }
    }

    private void handleRightClick(MouseInput input){

        Point2D gamePoint = canvasPainter.canvasPointToGamePoint(input.getX(), input.getY());

        Village clickedVillage = null;

        for(Sprite clickedSprite : currentState.getSpritesContaining(gamePoint)){
            if(clickedSprite instanceof Village){
                clickedVillage = (Village) clickedSprite;
                break;
            }
        }

        if(clickedVillage == null){
            rightClickMenu.setVisible(false);
            return;
        }

        villageClicked(clickedVillage);

        rightClickMenu.setVisible(true);
        AnchorPane.setLeftAnchor(rightClickMenu, input.getX());
        AnchorPane.setTopAnchor(rightClickMenu, input.getY());
    }

    /**
     * Handle some sprites being clicked
     * @param sprites the sprites that were clicked
     */
    private void spritesClicked(List<Sprite> sprites){
        //for now, only take the first sprite
        if(sprites.size() > 1){
            System.out.println("Multiple sprites clicked, only one handled");
        }

        Sprite sprite = sprites.get(sprites.size()-1);

        if(sprite instanceof Village){
            villageClicked((Village)sprite);
        }
        else if(sprite instanceof Mine){
            mineClicked((Mine)sprite);
        }
        else if(sprite instanceof Post){
            postClicked((Post) sprite);
        }
        else if(sprite instanceof WallSegment){
            wallClicked((WallSegment) sprite);
        }
        else if(sprite instanceof Tree){
            treeClicked((Tree) sprite);
        }
        else if(sprite instanceof Unit){
            unitClicked((Unit)sprite);
        }
    }

    private void villageClicked(Village village){
        if(village.getColor() != player.getColor()){
            return;
        }

        currentSelection.select(village);

        displayVillageInfo(village.getIndex());
    }

    /**
     * update the info pane to display info on the given village
     * @param villageIndex the index of the village to display info for
     */
    private void displayVillageInfo(int villageIndex){
        infoSwapPane.show(villagePane);

        Village village = currentInventory.getVillage(villageIndex);

        //display available upgrades
        villageUpgradesPane.getChildren().clear();
        for(VillageUpgrade upgrade : village.availableUpgrades()){
            villageUpgradesPane.getChildren().add(nodeFor(villageIndex, upgrade));
        }

        displayWorkShop(village);
    }

    /**
     * Create clickable node for the given upgrade
     *
     * @param villageIndex the index of village that this upgrade is for
     * @param upgrade the upgrade to make a node for
     * @return a node for the upgrade
     */
    private Node nodeFor(int villageIndex, VillageUpgrade upgrade){
        String label = String.format("%s (%d wood)",
                StringUtils.toCapitalCase(upgrade.name()), upgrade.getWoodPrice());
        Button upgradeButton = new Button(label);
        upgradeButton.setTooltip(new Tooltip(upgrade.getDescription()));


        upgradeButton.setOnAction(event -> {
            //upgrade the village
            player.takeAction(new UpgradeVillageAction(player.getColor(), villageIndex, upgrade));

            //once the village upgrade comes through, display the changes
            onStatePredicate(
                state -> state.getPlayerInventory(player).getVillage(villageIndex).hasUpgrade(upgrade),
                () -> displayVillageInfo(villageIndex));
        });
        return upgradeButton;
    }

    /**
     * @param item the item to get the node for
     * @return the node that allows you to buy a bench for the given item
     */
    private Node nodeFor(int villageIndex, WorkShopItem item){
        String label = String.format("%s (%d wood)",
                StringUtils.toCapitalCase(item.name()), item.getBenchPrice());
        Button benchButton = new Button(label);
        benchButton.setTooltip(new Tooltip(item.getDescription()));
        benchButton.setOnAction(event -> {
           player.takeAction(new UpgradeWorkShopAction(player.getColor(), villageIndex, item));

           //update display when the upgrade goes through
            onStatePredicate(
                    state -> state.getPlayerInventory(player).getVillage(villageIndex).getWorkShop().hasBench(item),
                    () -> displayVillageInfo(villageIndex)
            );
        });
        return benchButton;
    }

    /**
     * @param shop the work shop
     * @param item the item
     * @return a node that allows you to build the given item from the given workshop
     */
    private Node nodeFor(int villageIndex, WorkShop shop, WorkShopItem item){

        String buttonLabel = String.format("%s (%d wood)",
                StringUtils.toCapitalCase(item.name()), item.getItemPrice());
        Button button = new Button(buttonLabel);

        button.setTooltip(new Tooltip(item.getDescription()));

        button.setOnAction(event -> {
            player.takeAction(new BuildWorkShopItemAction(player.getColor(), villageIndex, item));
        });

        HBox box = new HBox();
        box.getChildren().addAll(button, itemStockLabels.get(item));
        return box;
    }

    private void displayWorkShop(Village village){
        //if the village has no shop, don't show it
        if(!village.hasUpgrade(VillageUpgrade.WORK_SHOP)){
            workShopPane.setVisible(false);
            return;
        }

        workShopPane.setVisible(true);

        WorkShop shop = village.getWorkShop();

        //display available benches
        shopUpgradesPane.getChildren().clear();
        for(WorkShopItem item : shop.availableBenches()){
            shopUpgradesPane.getChildren().add(nodeFor(village.getIndex(), item));
        }

        //display tools to purchase
        benchPane.getChildren().clear();
        for(WorkShopItem item : shop.getBenches()){
            benchPane.getChildren().add(nodeFor(village.getIndex(), shop, item));
        }
    }


    private void postClicked(Post post){
        if(post.getColor() != player.getColor()){
            return;
        }

        //if units are selected, send them here
        if(currentSelection.getType() == Selection.Type.UNITS){
            directBuildersTo(post.getIndex(), BuildType.POST);
            currentSelection.clear();
        }
        //if no post is selected, select this one
        else if(currentSelection.getType() != Selection.Type.POST){
            currentSelection.select(post);
        }

        //if we get this far, the selection is a post

        //if the selected post was clicked
        else if(currentSelection.contains(post)){
            currentSelection.clear();
        }
        //otherwise, build a wall between them
        else{
            System.out.println(String.format("Creating wall %d %d", currentSelection.getIndex(), post.getIndex()));
            this.player.takeAction(new CreateWallAction(player.getColor(), currentSelection.getIndex(), post.getIndex()));
            currentSelection.clear();
        }
    }

    private void wallClicked(WallSegment wallSegment){
        if(wallSegment.getColor() != player.getColor()){
            return;
        }

        //if units are selected, send them here
        if(currentSelection.getType() == Selection.Type.UNITS){
            directBuildersTo(wallSegment.getWall().getIndex(), BuildType.WALL);
            currentSelection.clear();
        }
    }

    private void mineClicked(Mine mine){
        //if units are selected, send them here
        if(currentSelection.getType() == Selection.Type.UNITS){
            directMinersTo(mine.getIndex());
            currentSelection.clear();
        }
    }

    private void treeClicked(Tree tree){
        //if units are selected, send them here
        if(currentSelection.getType() == Selection.Type.UNITS){
            directLumberjacksTo(tree.getIndex());
            currentSelection.clear();
        }
    }

    private void unitClicked(Unit unit){
        //if this is our player, select it
        if(unit.getColor() == player.getColor()) {
            selectUnit(unit);
        }
    }

    private void selectUnit(Unit unit){
        currentSelection.select(unit);

        if(unit instanceof Soldier){
            currentInteractMode = InteractMode.DIRECT_SOLDIER;
        }
    }

    /**
     * @return whether there is currently a soldier selected
     */
    private boolean soldierSelected(){
        if(currentSelection.getType() != Selection.Type.UNITS){
            return false;
        }

        for(int index : currentSelection.getIndices()){
            if(currentInventory.getUnit(index) instanceof Soldier){
                return true;
            }
        }

        return false;
    }

    /**
     * Send the selected units to the given project
     * @param index the index of the project to send builders to
     * @param type the type of build that this is
     */
    private void directBuildersTo(int index, BuildType type){
        for(int builderIndex : currentSelection.getIndices()){
            if(currentInventory.getUnit(builderIndex) instanceof Builder) {
                player.takeAction(new DirectBuilderAction(player.getColor(), builderIndex, index, type));
            }
        }
    }

    /**
     * Send the selected units to the given mine
     * @param index the index of the mine to send miners to
     */
    private void directMinersTo(int index){
        for(int minerIndex : currentSelection.getIndices()){
            if(currentInventory.getUnit(minerIndex) instanceof Miner) {
                player.takeAction(new DirectMinerAction(player.getColor(), minerIndex, index));
            }
        }
    }

    /**
     * Send the selected units to the given tree
     * @param index the index of the mine to send lumberjacks to
     */
    private void directLumberjacksTo(int index){
        for(int lumberjackIndex : currentSelection.getIndices()){
            if(currentInventory.getUnit(lumberjackIndex) instanceof Lumberjack) {
                player.takeAction(new DirectLumberjackAction(player.getColor(), lumberjackIndex, index));
            }
        }
    }

    /**
     * Send the selected units to the given mine
     * @param patrolArea the area to patrol
     */
    private void directSoldiersTo(PatrolArea patrolArea){
        //get the selected indices that are soldiers
        Set<Integer> soldierIndices = currentSelection.getIndices().stream().filter( index ->
                currentInventory.getUnit(index) instanceof Soldier).collect(Collectors.toSet());

        //direct them to the patrol area
        player.takeAction(new DirectSoldiersAction(player.getColor(), soldierIndices, patrolArea));
    }

    /**
     * Register a function to be called when a new state is received which matches the predicate
     * @param statePredicate the predicate that must be matched
     * @param listener the function to call
     */
    private void onStatePredicate(Predicate<GameState> statePredicate, StateReceivedListener listener){
        synchronized (statePredicates) {
            statePredicates.put(statePredicate, listener);
        }
    }

    /**
     * Call to indicate that a unit was lost
     * @param unitIndex the index of the lost unit
     */
    public void lostUnit(int unitIndex){
        currentSelection.lostUnit(unitIndex);
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public Selection getCurrentSelection(){
        return currentSelection;
    }

    public GUIPlayer getPlayer() {
        return player;
    }

    public PatrolArea getPatrolArea() {
        return patrolArea;
    }

    public RectangleArea getSelectionArea() {
        return selectionArea;
    }
}
