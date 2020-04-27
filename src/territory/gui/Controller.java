package territory.gui;

import territory.game.GameState;
import territory.game.Inventory;
import territory.game.action.player.*;
import territory.game.construction.*;
import territory.game.player.GUIPlayer;
import territory.game.sprite.Sprite;
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

import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML private Pane canvasPane;
    @FXML private Canvas canvas;
    @FXML private Label populationLabel;
    @FXML private Label stoneLabel;
    @FXML private Label goldLabel;

    private CanvasPainter canvasPainter;

    private GameState currentState;

    private InteractMode currentInteractMode;
    private double prevMouseX, prevMouseY;
    private int selectedVillageIndex = -1;
    private int selectedPostIndex = -1;
    private ArrayList<Integer> selectedUnitIndices = new ArrayList<>();

    private GUIPlayer player;

    public void init(){
        //resize the canvas with its pane
        this.canvas.widthProperty().bind(canvasPane.widthProperty());
        this.canvas.heightProperty().bind(canvasPane.heightProperty());

        this.canvasPainter = new CanvasPainter(this, canvas);

        canvas.setOnMouseClicked(this::handleCanvasMouseClick);
        canvas.setOnScroll(this::handleCanvasScroll);
        canvas.setOnMouseDragged(this::handleCanvasDragged);
        canvas.setOnMousePressed(this::handleCanvasMousePressed);
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
        stoneLabel.setText(""+inventory.getStone());
        goldLabel.setText(""+inventory.getGold());

        if(selectedVillageIndex == -1){
            populationLabel.setText("No village selected");
        }
        else{
            int villagePopulation = inventory.getVillage(selectedVillageIndex).getPopulation();
            populationLabel.setText(String.format("Population: %d", villagePopulation));
        }
    }

    @FXML
    public void trainMinerButtonClicked(ActionEvent actionEvent) {
        int numMiners = userDataInt(actionEvent);
        System.out.println("Miner " + numMiners);
        //we cannot train miners from no village
        if(selectedVillageIndex == -1){
            return;
        }

        player.takeAction(new TrainMinersAction(this.player.getColor(), selectedVillageIndex, numMiners));
    }

    @FXML
    public void trainBuilderButtonClicked(ActionEvent actionEvent) {
        System.out.println("Builder " + userDataInt(actionEvent));
        int numMiners = userDataInt(actionEvent);
        //we cannot train miners from no village
        if(selectedVillageIndex == -1){
            return;
        }

        player.takeAction(new TrainBuildersAction(this.player.getColor(), selectedVillageIndex, numMiners));
    }

    @FXML
    public void trainSoldierButtonClicked(ActionEvent actionEvent) {
        System.out.println("Soldier " + userDataInt(actionEvent));
    }

    @FXML
    public void placePostButtonClicked(ActionEvent actionEvent) {
        System.out.println("Place Post Mode");
        this.currentInteractMode = InteractMode.CREATE_POST;
    }

    @FXML
    public void placeVillageButtonClicked(ActionEvent actionEvent) {
        System.out.println("Place Village Mode");
        this.currentInteractMode = InteractMode.CREATE_VILLAGE;
    }

    @FXML
    public void scrollModeButtonClicked(ActionEvent actionEvent) {
        System.out.println("Scroll Mode");
        this.currentInteractMode = InteractMode.SCROLL;
    }


    private void handleCanvasDragged(MouseEvent e){
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
        if(currentInteractMode != InteractMode.SCROLL){
            return;
        }

        prevMouseX = e.getX();
        prevMouseY = e.getY();
    }

    private void handleCanvasMouseClick(MouseEvent e){
        //find the point that was clicked in the territory.game
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
            deselectAll();
            selectedVillageIndex = ((Village) sprite).getIndex();
            System.out.println("Selected village " + selectedVillageIndex);
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

    private void postClicked(Post post){
        //if units are selected, send them here
        if(selectedUnitIndices.size() > 0){
            directBuildersTo(post.getIndex(), BuildType.POST);
            deselectAll();
        }
        //if no post is selected, select this one
        else if(selectedPostIndex == -1){
            deselectAll();
            selectedPostIndex = post.getIndex();
            System.out.println("Selected post " + selectedPostIndex);
        }
        //if the selected post was clicked
        else if(selectedPostIndex == post.getIndex()){
            deselectAll();
        }
        //otherwise, build a wall between them
        else{
            System.out.println(String.format("Creating wall %d %d", selectedPostIndex, post.getIndex()));
            this.player.takeAction(new CreateWallAction(player.getColor(), selectedPostIndex, post.getIndex()));
        }
    }

    private void wallClicked(WallSegment wallSegment){
        //if units are selected, send them here
        if(selectedUnitIndices.size() > 0){
            directBuildersTo(wallSegment.getWall().getIndex(), BuildType.WALL);
            deselectAll();
        }
    }

    /**
     * Send the selected units to the given project
     * @param index the index of the project to send builders to
     * @param type the type of build that this is
     */
    private void directBuildersTo(int index, BuildType type){
        for(int builderIndex : selectedUnitIndices){
            player.takeAction(new DirectBuilderAction(player.getColor(), builderIndex, index, type));
        }
    }

    private void unitClicked(Unit unit){
        deselectNonUnits();
        selectedUnitIndices.add(unit.getIndex());
    }

    //deselect all selected objects
    private void deselectAll(){
        deselectNonUnits();
        selectedUnitIndices.clear();
    }

    //deselect everything execpt for units
    private void deselectNonUnits(){
        selectedVillageIndex = -1;
        selectedPostIndex = -1;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public int getSelectedVillageIndex() {
        return selectedVillageIndex;
    }

    public int getSelectedPostIndex() {
        return selectedPostIndex;
    }

    public ArrayList<Integer> getSelectedUnitIndices() {
        return selectedUnitIndices;
    }

    public GUIPlayer getPlayer() {
        return player;
    }
}
