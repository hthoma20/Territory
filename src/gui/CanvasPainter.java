package gui;

import game.GameState;
import game.Inventory;
import game.construction.Buildable;
import game.construction.Village;
import game.sprite.Sprite;
import game.unit.Unit;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

import javax.swing.*;
import java.util.Collection;

public class CanvasPainter {
    //coordinates of center of board
    private double centerX = 0, centerY = 0;

    //ratio of pixels per unit
    private double aspectRatio = 1;

    private Controller controller;

    private Canvas canvas;
    private GraphicsContext gc;
    private Paint background;

    //current items for convienience
    private GameState currentState;
    private Inventory currentInventory;

    public CanvasPainter(Controller controller, Canvas canvas){
        this.controller = controller;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.background = Color.AZURE;
    }

    /**
     * Paint the current state of the game
     */
    public void paint(){
        this.currentState = controller.getCurrentState();
        this.currentInventory = currentState.getPlayerInventory(controller.getPlayer());

        //fill background
        gc.setFill(background);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //transform canvas so that game coordinates may be used
        gc.save();
        this.transformCanvas();

        paintSprites(currentState.getAllSprites());
        paintSelection();

        gc.restore();
    }

    private void paintSprites(Collection<Sprite> sprites){
        for(Sprite sprite : sprites){
            if(sprite instanceof Buildable){
                paintBuildable(sprite);
            }
            else{
                paintSprite(sprite);
            }

        }
    }

    private void paintBuildable(Sprite sprite){
        boolean complete = ((Buildable)sprite).isComplete();

        if(!complete){
            gc.setGlobalAlpha(.5);
        }

        paintSprite(sprite);

        gc.setGlobalAlpha(1);
    }

    private void paintSprite(Sprite sprite){
        gc.save();

        Image image = sprite.getImage();

        gc.transform(new Affine(new Rotate(sprite.getRotation(), sprite.getX(), sprite.getY())));

        gc.drawImage(image, sprite.getX(), sprite.getY());

        gc.restore();
    }

    /**
     * Highlight selected items
     */
    public void paintSelection(){
        double highlightMargin = 2;
        double highlightOpacity = .2;

        //highlight selected village
        int villageIndex = controller.getSelectedVillageIndex();

        if(villageIndex == -1){
            return;
        }

        Village village = currentInventory.getVillage(villageIndex);

        gc.setFill(new Color(0, 1, 0, highlightOpacity));

        double x = village.getX() - highlightMargin;
        double y = village.getY() - highlightMargin;
        double width = village.getImage().getWidth() + 2*highlightMargin;
        double height = village.getImage().getHeight() + 2*highlightMargin;

        gc.fillRect(x, y, width, height);
    }

    /**
     * Apply transformations to paint on the canvas correctly
     */
    private void transformCanvas(){
        double expectedX = canvas.getWidth()/2;
        double expectedY = canvas.getHeight()/2;

        gc.translate(expectedX-centerX, expectedY-centerY);
        gc.scale(aspectRatio, aspectRatio);
    }

    /**
     * Given the location on the screen, return the location in the game
     * @param x x-coord of canvas point
     * @param y y-coord of canvas point
     * @return the game point represented by the canvas point
     */
    public Point2D canvasPointToGamePoint(double x, double y){
        Point2D canvasPoint = new Point2D(x, y);

        double expectedX = canvas.getWidth()/2;
        double expectedY = canvas.getHeight()/2;


        return canvasPoint.subtract(expectedX-centerX, expectedY-centerY).multiply(1/aspectRatio);
    }

    /**
     * Zoom in or out
     * @param delta the amount to zoom by
     */
    public void zoom(double delta){
        if(delta < 0){
            aspectRatio -= .1;
        }
        else if(delta > 0){
            aspectRatio += .1;
        }

        aspectRatio = bound(aspectRatio, 0, 10);
    }

    /**
     * Drag the canvas
     * @param deltaX the amount to move in the X direction
     * @param deltaY the amount to move in the Y direction
     */
    public void drag(double deltaX, double deltaY){
        centerX += deltaX;
        centerY += deltaY;
    }

    private double bound(double val, double min, double max){
        if(val < min){
            return min;
        }

        return Math.min(val, max);
    }
}
