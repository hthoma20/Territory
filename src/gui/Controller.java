package gui;

import game.GameState;
import game.Inventory;
import game.action.CreateVillageAction;
import game.action.GameAction;
import game.construction.Village;
import game.player.GUIPlayer;
import game.sprite.Sprite;
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

import java.util.Collection;
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
        System.out.println("Miner " + userDataInt(actionEvent));
    }

    @FXML
    public void trainBuilderButtonClicked(ActionEvent actionEvent) {
        System.out.println("Builder " + userDataInt(actionEvent));
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
        //find the point that was clicked in the game
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

        GameAction action = null;

        switch (currentInteractMode) {
            case CREATE_VILLAGE:
                System.out.println("Place Village");
                action = new CreateVillageAction(this.player, gamePoint.getX(), gamePoint.getY());
                break;
            case CREATE_POST:
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

        Sprite sprite = sprites.get(0);

        if(sprite instanceof Village){
            selectedVillageIndex = ((Village) sprite).getIndex();
            System.out.println("Selected village " + selectedVillageIndex);
        }
    }

    //deselect all selected objects
    private void deselectAll(){
        selectedVillageIndex = -1;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public int getSelectedVillageIndex() {
        return selectedVillageIndex;
    }

    public GUIPlayer getPlayer() {
        return player;
    }
}
