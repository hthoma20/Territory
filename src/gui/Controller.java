package gui;

import game.GameState;
import game.Inventory;
import game.action.CreateVillageAction;
import game.action.GameAction;
import game.player.GUIPlayer;
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

public class Controller {
    @FXML private Pane canvasPane;
    @FXML private Canvas canvas;
    @FXML private Label populationLabel;
    @FXML private Label stoneLabel;
    @FXML private Label goldLabel;

    private CanvasPainter canvasPainter;

    private InteractMode currentInteractMode;
    private double prevMouseX, prevMouseY;

    private GUIPlayer player;

    public void init(){
        //resize the canvas with its pane
        this.canvas.widthProperty().bind(canvasPane.widthProperty());
        this.canvas.heightProperty().bind(canvasPane.heightProperty());

        this.canvasPainter = new CanvasPainter(canvas);

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
        //paint the canvas
        Platform.runLater(()-> {
            canvasPainter.paintSprites(state.getAllSprites());
        });

        //update the labels
        Inventory inventory = this.player.getInventory();
        stoneLabel.setText(""+inventory.getStone());
        stoneLabel.setText(""+inventory.getGold());
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
        if(currentInteractMode == null){
            return;
        }

        GameAction action = null;

        switch (currentInteractMode) {
            case CREATE_VILLAGE:
                System.out.println("Place Village");
                Point2D gamePoint = canvasPainter.canvasPointToGamePoint(e.getX(), e.getY());
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
}
