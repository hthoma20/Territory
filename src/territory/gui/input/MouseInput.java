package territory.gui.input;

public class MouseInput {

    //the coordinates of the cursor when this input was recieved
    private double x, y;

    public MouseInput(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
