package territory.gui;

import javafx.scene.Scene;
import territory.game.*;
import territory.game.action.player.*;
import territory.game.construction.*;
import territory.game.player.GUIPlayer;
import territory.game.sprite.Sprite;
import territory.game.target.BuildType;
import territory.game.target.PatrolArea;
import territory.game.unit.Unit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

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
    private double prevMouseX, prevMouseY;

    private Selection currentSelection = new Selection();

    private Point2D patrolAreaCenter = null;
    private Point2D selectionPoint = null;

    private Point2D mousePoint;

    private GUIPlayer player;

    public void init(Scene scene){
        this.scene = scene;

        //resize the canvas with its pane
        this.canvas.widthProperty().bind(canvasPane.widthProperty());
        this.canvas.heightProperty().bind(canvasPane.heightProperty());

        this.canvasPainter = new CanvasPainter(this, canvas);

        this.currentInteractMode = InteractMode.SELECT;

        canvas.setOnMouseClicked(this::handleCanvasMouseClick);
        canvas.setOnScroll(this::handleCanvasScroll);
        canvas.setOnMouseDragged(this::handleCanvasDragged);
        canvas.setOnMousePressed(this::handleCanvasMousePressed);
        canvas.setOnMouseReleased(this::handleCanvasMouseReleased);
        canvas.setOnMouseMoved(e -> {
            mousePoint = canvasPainter.canvasPointToGamePoint(e.getX(), e.getY());
        });
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
        System.out.println("Setting interaction mode to " + currentInteractMode);
    }

    private void handleCanvasDragged(MouseEvent e){

        mousePoint = canvasPainter.canvasPointToGamePoint(e.getX(), e.getY());

        if(currentInteractMode != InteractMode.SCROLL){
            return;
        }

        double deltaX = prevMouseX - e.getX();
        double deltaY = prevMouseY - e.getY();

        canvasPainter.drag(deltaX, deltaY);

        prevMouseX = e.getX();
        prevMouseY = e.getY();
    }

    private void handleCanvasScroll(ScrollEvent e){
        canvasPainter.zoom(e.getDeltaY());
    }

    private void handleCanvasMousePressed(MouseEvent e){
        switch(currentInteractMode){
            case SELECT:
                selectionPoint = mousePoint;
                break;
            case SCROLL:
                prevMouseX = e.getX();
                prevMouseY = e.getY();
                break;
        }
    }

    private void handleCanvasMouseReleased(MouseEvent e){
        if(currentInteractMode != InteractMode.SELECT){
            return;
        }

        //select all units in selected rectangle
        RectangleArea selection = new RectangleArea(selectionPoint, mousePoint);

        for(Unit unit : currentState.getAllUnitsInArea(selection)){
            if(unit.getColor() != player.getColor()){
                continue;
            }

            currentSelection.select(unit);
        }

        selectionPoint = null;
    }

    private void handleCanvasMouseClick(MouseEvent e){
        //find the point that was clicked in the territory game
        Point2D gamePoint = canvasPainter.canvasPointToGamePoint(e.getX(), e.getY());

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
            case SCROLL:
                break;
            case DIRECT_SOLDIER:
                directSoldier(gamePoint);
                break;
            case SELECT:
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

    private void directSoldier(Point2D point){
        if(patrolAreaCenter == null){
            patrolAreaCenter = point;
        }
        else{
            double radius = patrolAreaCenter.distance(point);
            PatrolArea patrolArea =
                    new PatrolArea(this.player.getColor(), patrolAreaCenter.getX(), patrolAreaCenter.getY(), radius);
            directSoldiersTo(patrolArea);
            currentSelection.clear();
            patrolAreaCenter = null;
        }

    }

    /**
     * Send the selected units to the given project
     * @param index the index of the project to send builders to
     * @param type the type of build that this is
     */
    private void directBuildersTo(int index, BuildType type){
        for(int builderIndex : currentSelection.getIndices()){
            player.takeAction(new DirectBuilderAction(player.getColor(), builderIndex, index, type));
        }
    }

    /**
     * Send the selected units to the given mine
     * @param index the index of the mine to send miners to
     */
    private void directMinersTo(int index){
        for(int minerIndex : currentSelection.getIndices()){
            player.takeAction(new DirectMinerAction(player.getColor(), minerIndex, index));
        }
    }

    /**
     * Send the selected units to the given mine
     * @param patrolArea the area to patrol
     */
    private void directSoldiersTo(PatrolArea patrolArea){
        for(int soldierIndex : currentSelection.getIndices()){
            player.takeAction(new DirectSoldierAction(player.getColor(), soldierIndex, patrolArea));
        }
    }

    /**
     * Call to indicate that a unit was lost
     * @param unitIndex the index of the lost unit
     */
    public void lostUnit(int unitIndex){
        //we only care if this unit was selected
        if(currentSelection.getType() != Selection.Type.UNITS){
            return;
        }

        currentSelection.lostUnit(unitIndex);
    }

    private void unitClicked(Unit unit){
        //if this is our player, select it
        if(unit.getColor() == player.getColor()) {
            currentSelection.select(unit);


        }
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

    public Point2D getPatrolAreaCenter() {
        return patrolAreaCenter;
    }

    public Point2D getSelectionPoint() {
        return selectionPoint;
    }

    public Point2D getMousePoint() {
        return mousePoint;
    }
}
