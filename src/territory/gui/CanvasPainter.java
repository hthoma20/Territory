package territory.gui;

import javafx.scene.text.TextAlignment;
import territory.game.*;
import territory.game.construction.Village;
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
import territory.game.target.PatrolArea;
import territory.game.unit.Soldier;
import territory.game.unit.Unit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CanvasPainter {
    //coordinates of center of board
    private double centerX = 0, centerY = 0;

    //ratio of pixels per unit
    private double aspectRatio = 1;
    private final double MIN_ASPECT_RATIO = .5;
    private final double MAX_ASPECT_RATIO = 2;

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
        this.background = Color.CORNSILK;
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

        paintAreaInPlay();
        paintTerritories(currentState.getPlayerTerritories());
        paintSprites(currentState.getAllSprites());
        paintSelection();
        paintSelectBox();
        paintPatrolArea();

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

        //rotate the image according to the sprites rotation
        gc.transform(new Affine(new Rotate(sprite.getRotation(), sprite.getX(), sprite.getY())));

        gc.drawImage(image, sprite.getTopX(), sprite.getTopY());

        gc.restore();

        if(sprite instanceof Village){
            int population = ((Village) sprite).getPopulation();
            double x = sprite.getX();
            double y = sprite.getY() + sprite.getHeight()/2 + 15;
            gc.setTextAlign(TextAlignment.CENTER);
            gc.strokeText("Pop. " + population, x, y);
        }
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
                    Unit unit;
                    try {
                        unit = currentInventory.getUnit(index);
                    }
                    catch(IndexOutOfBoundsException exc){
                        System.out.println("Out of bounds in CanvasPainter.paintSelection");
                        continue;
                    }

                    highlightSprite(unit);

                    if(unit instanceof Soldier){
                        PatrolArea area = ((Soldier) unit).getPatrolArea();
                        if(area != null){
                            strokeCircle(area.getX(), area.getY(), area.getRadius());
                        }
                    }
                }
                return;
        }
    }

    private void paintPatrolArea(){
        PatrolArea patrolArea = controller.getPatrolArea();

        if(patrolArea == null){
            return;
        }

        gc.setStroke(Color.DARKOLIVEGREEN);
        strokeCircle(patrolArea.getX(), patrolArea.getY(), patrolArea.getRadius());
    }

    private void paintSelectBox(){
        RectangleArea selection = controller.getSelectionArea();

        if(selection == null){
            return;
        }

        gc.setStroke(Color.BLUEVIOLET);
        gc.strokeRect(selection.getTopX(), selection.getTopY(), selection.getWidth(), selection.getHeight());
    }

    private void paintAreaInPlay(){
        gc.save();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3/aspectRatio);

        RectangleArea area = currentState.getAreaInPlay();
        gc.strokeRect(area.getTopX(), area.getTopY(), area.getWidth(), area.getHeight());
        gc.restore();
    }

    private void strokeCircle(double x, double y, double radius){
        gc.strokeOval(x - radius, y - radius, 2*radius, 2*radius);
    }

    private void highlightSprite(Sprite sprite){

        double x = sprite.getTopX() - highlightMargin;
        double y = sprite.getTopY() - highlightMargin;
        double width = sprite.getWidth() + 2*highlightMargin;
        double height = sprite.getHeight() + 2*highlightMargin;

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
     * @param x the x-coordinate to respect when zooming
     * @param y the y-coordinate to respect when zooming
     */
    public void zoom(double delta, double x, double y){
        //adjust delta
        if(delta < 0){
            delta = -.1;
        }
        else{
            delta = .1;
        }

        //if the zoom doesn't violate min or max zoom, do the adjustment
        if(inRange(aspectRatio+delta, MIN_ASPECT_RATIO, MAX_ASPECT_RATIO)){
            aspectRatio += delta;
            centerX += x * delta;
            centerY += y * delta;
        }
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

    /**
     * Fit the canvas to match the given area
     * @param area the area to fit the canvas to
     */
    public void fitToArea(RectangleArea area){

        centerX = area.getTopX() + area.getWidth()/2;
        centerY = area.getTopY() + area.getHeight()/2;

        double widthRatio = canvas.getWidth()/area.getWidth();
        double heightRatio = canvas.getHeight()/area.getHeight();

        aspectRatio = Math.min(widthRatio, heightRatio);
    }

    private boolean inRange(double val, double min, double max){
        return min <= val && val <= max;
    }
}
