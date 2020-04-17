package gui;

import game.sprite.Sprite;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Collection;

public class CanvasPainter {
    //coordinates of center of board
    private double centerX = 0, centerY = 0;

    private Point2D circle = new Point2D(0, 0);

    //ratio of pixels per unit
    private double aspectRatio = 1;

    private Canvas canvas;
    private GraphicsContext gc;
    private Paint background;

    public CanvasPainter(Canvas canvas){
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.background = Color.AZURE;
    }

    public void paintSprites(Collection<Sprite> sprites){
        gc.setFill(background);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();

        this.transformCanvas();

        for(Sprite sprite : sprites){
            Image image = sprite.getImage();
            gc.drawImage(image, sprite.getX(), sprite.getY());
        }

        gc.restore();
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
