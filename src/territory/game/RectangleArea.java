package territory.game;

import javafx.geometry.Point2D;

import java.io.Serializable;

/**
 * Defines a rectanglular area of the map
 */
public class RectangleArea implements MapArea, Serializable {
    //point 1 is the upper left point
    private double x1, y1;
    //point 2 is the lower right point
    private double x2, y2;

    public RectangleArea(double x1, double y1, double x2, double y2){
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);

        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
    }

    public RectangleArea(Point2D p1, Point2D p2){
        this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public RectangleArea(RectangleArea src){
        this.x1 = src.x1;
        this.y1 = src.y1;
        this.x2 = src.x2;
        this.y2 = src.y2;
    }

    @Override
    public RectangleArea copy(){
        return new RectangleArea(this);
    }

    public double getTopX(){
        return x1;
    }

    public double getTopY(){
        return y1;
    }

    public double getWidth(){
        return x2 - x1;
    }

    public double getHeight(){
        return y2 - y1;
    }

    @Override
    public boolean contains(double x, double y){
        return (x1 <= x && x <= x2) && (y1 <= y && y <= y2);
    }
}
