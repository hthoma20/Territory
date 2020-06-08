package territory.gui;

import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import territory.game.*;
import territory.game.action.player.*;
import territory.game.construction.*;
import territory.game.player.GUIPlayer;
import territory.game.sprite.Sprite;
import territory.game.target.BuildType;
import territory.game.target.PatrolArea;
import territory.game.unit.Builder;
import territory.game.unit.Miner;
import territory.game.unit.Soldier;
import territory.game.unit.Unit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import territory.gui.input.InputProcessor;
import territory.gui.input.MouseDragInput;
import territory.gui.input.MouseInput;
import territory.gui.input.MouseScrollInput;

import java.util.List;

public class Controller {
    @FXML private Pane canvasPane;
    @FXML private Canvas canvas;
    @FXML private Label populationLabel;
    @FXML private Label stoneLabel;
    @FXML private Label goldLabel;
    @FXML private Label territoryLabel;

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


        InputProcessor inputProcessor = new InputProcessor(scene, canvas);

        inputProcessor.setOnScroll(this::handleCanvasScroll);
        inputProcessor.setOnMiddleDrag(this::handleMiddleDrag);
        inputProcessor.setOnRightDrag(this::handleRightDrag);
        inputProcessor.setOnLeftDrag(this::handleLeftDrag);
        inputProcessor.setOnLeftRelease(this::handleLeftReleased);
        inputProcessor.setOnRightRelease(this::handleRightReleased);
        inputProcessor.setOnLeftClick(this::handleLeftClick);
    }

    public void setPlayer(GUIPlayer player){
        this.player = player;
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

    public void updateDisplay(GameState state){
        currentState = state;

        Platform.runLater(()-> {
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
        territoryLabel.setText(String.format("%,d", (int)(territories.area()/1000)));

        if(currentSelection.getType() != Selection.Type.VILLAGE){
            populationLabel.setText("No village selected");
        }
        else{
            int villagePopulation = inventory.getVillage(currentSelection.getIndex()).getPopulation();
            populationLabel.setText(String.format("Population: %d", villagePopulation));
        }
    }

    @FXML
    public void trainMinerButtonClicked(ActionEvent actionEvent) {
        int numMiners = userDataInt(actionEvent);
        System.out.println("Miner " + numMiners);
        //we cannot train miners from no village
        if(currentSelection.getType() != Selection.Type.VILLAGE){
            return;
        }

        player.takeAction(new TrainMinersAction(this.player.getColor(), currentSelection.getIndex(), numMiners));
    }

    @FXML
    public void trainBuilderButtonClicked(ActionEvent actionEvent) {
        System.out.println("Builder " + userDataInt(actionEvent));
        int numBuilders = userDataInt(actionEvent);
        //we cannot train builders from no village
        if(currentSelection.getType() != Selection.Type.VILLAGE){
            return;
        }

        player.takeAction(new TrainBuildersAction(this.player.getColor(), currentSelection.getIndex(), numBuilders));
    }

    @FXML
    public void trainSoldierButtonClicked(ActionEvent actionEvent) {
        System.out.println("Soldier " + userDataInt(actionEvent));
        int numSoldiers = userDataInt(actionEvent);
        //we cannot train soldiers from no village
        if(currentSelection.getType() != Selection.Type.VILLAGE){
            return;
        }

        player.takeAction(new TrainSoldiersAction(this.player.getColor(), currentSelection.getIndex(), numSoldiers));
    }

    @FXML
    public void interactModeButtonClicked(ActionEvent actionEvent){
        String mode = userDataString(actionEvent);
        this.currentInteractMode = InteractMode.valueOf(mode);
        actionEvent.getTarget();
        System.out.println("Setting interaction mode to " + currentInteractMode);
    }

    private void handleCanvasScroll(MouseScrollInput scrollInput){
        Point2D scrollPoint = canvasPainter.canvasPointToGamePoint(scrollInput.getX(), scrollInput.getY());
        canvasPainter.zoom(scrollInput.getDeltaY(), scrollPoint.getX(), scrollPoint.getY());
    }

    private void handleMiddleDrag(MouseDragInput dragInput){
        //pan the canvas
        canvasPainter.drag(-dragInput.getDeltaX(), -dragInput.getDeltaY());
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
        List<Sprite> clickedSprites = currentState.getSpritesContaining(gamePoint.getX(), gamePoint.getY());

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
        else if(sprite instanceof Unit){
            unitClicked((Unit)sprite);
        }
    }

    private void villageClicked(Village village){
        if(village.getColor() != player.getColor()){
            return;
        }

        currentSelection.select((Village)village);
        System.out.println("Selected village " + ((Village) village).getIndex());
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
            System.out.println("Selected post " + post.getIndex());
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
     * Send the selected units to the given mine
     * @param patrolArea the area to patrol
     */
    private void directSoldiersTo(PatrolArea patrolArea){
        for(int soldierIndex : currentSelection.getIndices()){
            if(getUnit(soldierIndex) instanceof Soldier) {
                player.takeAction(new DirectSoldierAction(player.getColor(), soldierIndex, patrolArea));
            }
        }
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
