package territory.gui;

import territory.game.*;
import territory.game.construction.Buildable;
import territory.game.sprite.Sprite;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //configurations for highlighting things
    private double highlightMargin = 2;
    private double highlightOpacity = .2;

    private HashMap<GameColor, Paint> territoryPaint = new HashMap<GameColor, Paint>(){{
        put(GameColor.PURPLE, Color.rgb(147, 11, 232, .7));
        put(GameColor.GREEN, Color.rgb(4, 147, 47, .7));
    }};

    public CanvasPainter(Controller controller, Canvas canvas){
        this.controller = controller;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.background = Color.AZURE;
    }

    /**
     * Paint the current state of the territory.game
     */
    public void paint(){
        this.currentState = controller.getCurrentState();
        this.currentInventory = currentState.getPlayerInventory(controller.getPlayer());

        //fill background
        gc.setFill(background);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //transform canvas so that territory.game coordinates may be used
        gc.save();
        this.transformCanvas();

        paintTerritories(currentState.getPlayerTerritories());
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

        double centerX = sprite.getX() + image.getWidth()/2;
        double centerY = sprite.getY() + image.getHeight()/2;

        gc.transform(new Affine(new Rotate(sprite.getRotation(), centerX, centerY)));

        gc.drawImage(image, sprite.getX(), sprite.getY());

        gc.restore();
    }

    /**
     * Highlight selected items
     */
    private void paintSelection(){

        gc.setFill(new Color(0, 1, 0, highlightOpacity));

        Selection selection = controller.getCurrentSelection();
        switch(selection.getType()){
            case NONE:
                return;
            case VILLAGE:
                highlightSprite(currentInventory.getVillage(selection.getIndex()));
                return;
            case POST:
                highlightSprite(currentInventory.getPost(selection.getIndex()));
                return;
            case UNITS:
                for(int index : selection.getIndices()){
                    highlightSprite(currentInventory.getUnit(index));
                }
                return;
        }
    }

    private void highlightSprite(Sprite sprite){
        double x = sprite.getX() - highlightMargin;
        double y = sprite.getY() - highlightMargin;
        double width = sprite.getImage().getWidth() + 2*highlightMargin;
        double height = sprite.getImage().getHeight() + 2*highlightMargin;

        gc.fillRect(x, y, width, height);
    }

    private void paintTerritories(List<TerritoryList> allTerritories){
        for(TerritoryList territories : allTerritories){
            for(Territory territory : territories){
                gc.setFill(territoryPaint.get(territory.getColor()));
                gc.fillPolygon(territory.getXPoints(), territory.getYPoints(), territory.getNumPoints());
            }
        }
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
     * Given the location on the screen, return the location in the territory.game
     * @param x x-coord of canvas point
     * @param y y-coord of canvas point
     * @return the territory.game point represented by the canvas point
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
