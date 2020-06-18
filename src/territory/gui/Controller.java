package territory.gui;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import territory.game.*;
import territory.game.action.player.*;
import territory.game.construction.*;
import territory.game.construction.upgrade.VillageUpgrade;
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

import java.util.*;
import java.util.stream.Collectors;

public class Controller {

    @FXML private Pane canvasPane;
    @FXML private Canvas canvas;

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

    //info pane
    @FXML private SwapPane infoSwapPane;
    @FXML private Pane trainUnitsPane;
    @FXML private Pane villagePane;
    @FXML private Pane villageUpgradesPane;
    @FXML private Pane shopUpgradesPane;
    @FXML private Pane buildPane;

    //labels for prices of things on the GUI
    @FXML private Label minerPriceLabel1;
    @FXML private Label minerPriceLabel5;
    @FXML private Label minerPriceLabel10;
    @FXML private Label lumberjackPriceLabel1;
    @FXML private Label lumberjackPriceLabel5;
    @FXML private Label lumberjackPriceLabel10;
    @FXML private Label builderPriceLabel1;
    @FXML private Label builderPriceLabel5;
    @FXML private Label builderPriceLabel10;
    @FXML private Label soldierPriceLabel1;
    @FXML private Label soldierPriceLabel5;
    @FXML private Label soldierPriceLabel10;
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

    private InteractMode currentInteractMode;

    private Selection currentSelection = new Selection();

    private PatrolArea patrolArea;
    private RectangleArea selectionArea;


    private GUIPlayer player;

    public void init(Scene scene){
        this.scene = scene;

        //resize the canvas with its pane
        this.canvas.widthProperty().bind(canvasPane.widthProperty());
        this.canvas.heightProperty().bind(canvasPane.heightProperty());

        this.canvasPainter = new CanvasPainter(this, canvas);

        this.currentInteractMode = InteractMode.CREATE_VILLAGE;

        infoSwapPane.show(trainUnitsPane);

        setPriceLabels();

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
        this.cursorMap = new HashMap<>(2);

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

        Platform.runLater(()-> {
            canvasPainter.fitToArea(state.getAreaInPlay());
            //paint the canvas
            canvasPainter.paint();

            updateLabels();
        });
    }

    private void updateLabels(){
        //update the labels
        Inventory inventory = currentState.getPlayerInventory(this.player);
        TerritoryList territories = currentState.getPlayerTerritories(this.player.getIndex());
        stoneLabel.setText(""+inventory.getStone());
        goldLabel.setText(""+inventory.getGold());
        woodLabel.setText(""+inventory.getWood());
        territoryLabel.setText(String.format("%,d", (int)(territories.area()/1000)));

        updateCountLabels();
    }

    private void updateCountLabels(){
        Inventory inventory = currentState.getPlayerInventory(player);

        int minerCount = 0;
        int lumberjackCount = 0;
        int builderCount = 0;
        int soldierCount = 0;

        for(Unit unit : inventory.getUnits()){
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

        int villageCount = inventory.getVillages().size();
        villagesLabel.setText("" + villageCount);

        int population = 0;
        for(Village village : inventory.getVillages()){
            population += village.getPopulation();
        }
        populationLabel.setText("" + population);
    }

    private void setPriceLabels(){
        int minerGold = Miner.getGoldPrice();
        int lumberjackGold = Lumberjack.getGoldPrice();
        int builderGold = Builder.getGoldPrice();
        int soldierGold = Soldier.getGoldPrice();
        int villageGold = Village.getGoldPrice();
        int postGold = Post.getGoldPrice();

        minerPriceLabel1.setText("" + minerGold);
        minerPriceLabel5.setText("" + minerGold*5);
        minerPriceLabel10.setText("" + minerGold*10);

        lumberjackPriceLabel1.setText("" + lumberjackGold);
        lumberjackPriceLabel5.setText("" + lumberjackGold*5);
        lumberjackPriceLabel10.setText("" + lumberjackGold*10);

        builderPriceLabel1.setText("" + builderGold);
        builderPriceLabel5.setText("" + builderGold*5);
        builderPriceLabel10.setText("" + builderGold*10);

        soldierPriceLabel1.setText("" + soldierGold);
        soldierPriceLabel5.setText("" + soldierGold*5);
        soldierPriceLabel10.setText("" + soldierGold*10);

        villagePriceLabel.setText("" + villageGold);
        postPriceLabel.setText("" + postGold);
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

        System.out.println(scrollInput.getDeltaY() + " " + delta);

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

        for(Sprite sprite : currentState.getSpritesContaining(gamePoint)){
            if(sprite instanceof Village){
                clickedVillage = (Village) sprite;
            }
        }

        if(clickedVillage == null){
            return;
        }

        //create a context menu for the clicked village
        ContextMenu menu = new ContextMenu();

        for(VillageUpgrade availableUpgrade : clickedVillage.availableUpgrades()){
            String label = String.format("%s (%d wood)", availableUpgrade.name(), availableUpgrade.getWoodPrice());
            MenuItem item = new MenuItem(label);
            item.setUserData(availableUpgrade);
            menu.getItems().add(item);
        }

        menu.getItems().add(new MenuItem("Close Menu"));

        menu.setOnAction(this::handleVillageMenuClicked);
        menu.setUserData(clickedVillage);

        menu.show(canvas, input.getX(), input.getY());
    }

    private void handleVillageMenuClicked(ActionEvent event){
        ContextMenu menu = (ContextMenu)event.getSource();
        MenuItem itemClicked = (MenuItem)event.getTarget();

        Object itemData = itemClicked.getUserData();

        if(!(itemData instanceof VillageUpgrade)){
            return;
        }

        Village village = (Village)menu.getUserData();
        VillageUpgrade upgrade = (VillageUpgrade)itemData;

        player.takeAction(new UpgradeVillageAction(player.getColor(), village.getIndex(), upgrade));
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

        currentSelection.select((Village)village);

        displayVillageInfo(village);
    }

    /**
     * update the info pane to display info on the given village
     * @param village the village to display info for
     */
    private void displayVillageInfo(Village village){
        //infoSwapPane.show(villagePane);

        //display available upgrades
        villageUpgradesPane.getChildren().clear();
        for(VillageUpgrade upgrade : village.availableUpgrades()){
            villageUpgradesPane.getChildren().add(upgradeNode(upgrade));
        }
    }

    /**
     * Create clickable node for the given upgrade
     * @param upgrade the upgrade to make a node for
     * @return a node for the upgrade
     */
    private Node upgradeNode(VillageUpgrade upgrade){
        Button upgradeButton = new Button(toCapitalCase(upgrade.name()));
        upgradeButton.setTooltip(new Tooltip(upgrade.getDescription()));
        return upgradeButton;
    }

    /**
     * Capitalize the given string, for example
     * HELLO THERE -> Hello there
     * @param str the string to capitalize
     * @return str, but capitalized
     */
    private String toCapitalCase(String str){
        if(str.length() < 1){
            return str;
        }

        char first = str.charAt(0);
        String rest = str.substring(1);

        return Character.toUpperCase(first) + rest.toLowerCase();
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
            if(getUnit(index) instanceof Soldier){
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
            if(getUnit(builderIndex) instanceof Builder) {
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
            if(getUnit(minerIndex) instanceof Miner) {
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
            if(getUnit(lumberjackIndex) instanceof Lumberjack) {
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
                getUnit(index) instanceof Soldier).collect(Collectors.toSet());

        //direct them to the patrol area
        player.takeAction(new DirectSoldiersAction(player.getColor(), soldierIndices, patrolArea));
    }

    private Unit getUnit(int unitIndex){
        return currentState.getPlayerInventory(player).getUnit(unitIndex);
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
