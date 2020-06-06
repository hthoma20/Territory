package territory.gui.input;

public class MouseScrollInput extends MouseInput {
    //the amount by which the scroll wheel was turned
    private double deltaY;

    public MouseScrollInput(double deltaY, double mouseX, double mouseY) {
        super(mouseX, mouseY);

        this.deltaY = deltaY;
    }

    public double getDeltaY() {
        return deltaY;
    }
}
