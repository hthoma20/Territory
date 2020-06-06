package territory.gui.input;

public class MouseDragInput extends MouseInput {
    //coordinates of drag start
    private double startX, startY;

    //coordinates of previous position in drag
    private double prevX, prevY;

    public MouseDragInput(double startX, double startY, double prevX, double prevY, double x, double y) {
        super(x, y);

        this.startX = startX;
        this.startY = startY;
        this.prevX = prevX;
        this.prevY = prevY;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getPrevX() {
        return prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public double getDeltaX(){
        return getX() - prevX;
    }

    public double getDeltaY(){
        return getY() - prevY;
    }
}
