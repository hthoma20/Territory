package territory.gui.input;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.function.Consumer;

/**
 * Helps to process user input
 * various listeners can be attatched to this input processor
 *
 * Holding crtl makes clicks act like right clicks
 * Holding shift makes clicks act like middle clicks
 */
public class InputProcessor {

    //the current location of the cursor on the canvas
    private double mouseX, mouseY;

    //previous location of cursor during a drag
    private double dragStartMouseX, dragStartMouseY;
    private double prevMouseX, prevMouseY;

    private InputModifier modifier = null;

    //input handlers to be called on various events
    private Consumer<MouseDragInput> onLeftDrag;
    private Consumer<MouseDragInput> onRightDrag;
    private Consumer<MouseDragInput> onMiddleDrag;

    private Consumer<MouseScrollInput> onScroll;

    private Consumer<MouseInput> onLeftPress;
    private Consumer<MouseInput> onRightPress;
    private Consumer<MouseInput> onMiddlePress;

    private Consumer<MouseInput> onLeftRelease;
    private Consumer<MouseInput> onRightRelease;
    private Consumer<MouseInput> onMiddleRelease;

    private Consumer<MouseInput> onRightClick;
    private Consumer<MouseInput> onLeftClick;
    private Consumer<MouseInput> onMiddleClick;

    private Consumer<MouseInput> onMouseMove;

    /**
     * Construct an input processor that processes inputs to the given
     * scene and canvas
     * @param scene the scene to detect keyboard inputs
     * @param canvas the canvas to process mouse inputs
     */
    public InputProcessor(Scene scene, Canvas canvas){
        canvas.setOnMouseClicked(this::handleCanvasClicked);
        canvas.setOnScroll(this::handleCanvasScroll);
        canvas.setOnMouseDragged(this::handleCanvasDragged);
        canvas.setOnMousePressed(this::handleCanvasPressed);
        canvas.setOnMouseReleased(this::handleCanvasReleased);
        canvas.setOnMouseMoved(this::handleCanvasMouseMoved);
    }

    ////////////////////////////////////////////////////////
    ///////////////// Event Handlers ///////////////////////
    ////////////////////////////////////////////////////////

    private void handleCanvasClicked(MouseEvent e){
        //only counts as a click if the press and release were in the same place
        // (within 5 px)
        if(distance(dragStartMouseX, dragStartMouseY, mouseX, mouseY) > 5){
            return;
        }

        Consumer<MouseInput> listener = clickListenerForEvent(e);
        if(listener != null){
            listener.accept(new MouseInput(e.getX(), e.getY()));
        }

        modifier = null;
    }

    private void handleCanvasPressed(MouseEvent e){
        //a press signals the start of a drag
        dragStartMouseX = e.getX();
        dragStartMouseY = e.getY();

        prevMouseX = e.getX();
        prevMouseY = e.getY();

        modifier = modifierForEvent(e);

        Consumer<MouseInput> listener = pressListenerForEvent(e);
        if(listener != null){
            listener.accept(new MouseInput(e.getX(), e.getY()));
        }
    }

    private void handleCanvasReleased(MouseEvent e){
        Consumer<MouseInput> listener = releaseListenerForEvent(e);
        if(listener != null){
            listener.accept(new MouseInput(e.getX(), e.getY()));
        }
    }

    private void  handleCanvasDragged(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();

        Consumer<MouseDragInput> listener = dragListenerForEvent(e);

        //if no one cares, don't do anything else
        if(listener == null){
            return;
        }

        MouseDragInput input =
                new MouseDragInput(dragStartMouseX, dragStartMouseY, prevMouseX, prevMouseY, mouseX, mouseY);

        listener.accept(input);

        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    private void handleCanvasMouseMoved(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();

        if(onMouseMove != null){
            onMouseMove.accept(new MouseInput(mouseX, mouseY));
        }
    }

    private void handleCanvasScroll(ScrollEvent e){
        if(onScroll == null){
            return;
        }

        onScroll.accept(new MouseScrollInput(e.getDeltaY(), mouseX, mouseY));
    }

    ////////////////////////////////////////////////////////
    ///////////////// Helpers //////////////////////////////
    ////////////////////////////////////////////////////////

    /**
     * @param e the mouse button that was dragged
     * @return the listener that should be notified that the given button was dragged
     */
    private Consumer<MouseDragInput> dragListenerForEvent(MouseEvent e){
        //if there is a modifier, use that
        if(modifier != null){
            switch(modifier){
                case CTRL:
                    return onRightDrag;
                case SHIFT:
                    return onMiddleDrag;
            }
        }

        //otherwise, pick based on button
        switch(e.getButton().name()){
            case "PRIMARY":
                return onLeftDrag;
            case "SECONDARY":
                return onRightDrag;
            case "MIDDLE":
                return onMiddleDrag;
            default:
                System.out.println("Unknown mouse button");
                return null;
        }
    }

    private Consumer<MouseInput> pressListenerForEvent(MouseEvent e){
        //if there is a modifier, use that
        if(modifier != null){
            switch(modifier){
                case CTRL:
                    return onRightPress;
                case SHIFT:
                    return onMiddlePress;
            }
        }

        switch(e.getButton().name()){
            case "PRIMARY":
                return onLeftPress;
            case "SECONDARY":
                return onRightPress;
            case "MIDDLE":
                return onMiddlePress;
            default:
                System.out.println("Unknown mouse button");
                return null;
        }
    }

    private Consumer<MouseInput> releaseListenerForEvent(MouseEvent e){
        if(modifier != null){
            switch(modifier){
                case CTRL:
                    return onRightRelease;
                case SHIFT:
                    return onMiddleRelease;
            }
        }

        switch(e.getButton().name()){
            case "PRIMARY":
                return onLeftRelease;
            case "SECONDARY":
                return onRightRelease;
            case "MIDDLE":
                return onMiddleRelease;
            default:
                System.out.println("Unknown mouse button");
                return null;
        }
    }

    private Consumer<MouseInput> clickListenerForEvent(MouseEvent e){
        if(modifier != null){
            switch(modifier){
                case CTRL:
                    return onRightClick;
                case SHIFT:
                    return onMiddleClick;
            }
        }

        switch(e.getButton().name()){
            case "PRIMARY":
                return onLeftClick;
            case "SECONDARY":
                return onRightClick;
            case "MIDDLE":
                return onMiddleClick;
            default:
                System.out.println("Unknown mouse button");
                return null;
        }
    }

    /**
     * @param e the mouse event
     * @return the relevant modifier from the event
     */
    private InputModifier modifierForEvent(MouseEvent e){
        if(e.isControlDown()){
            return InputModifier.CTRL;
        }
        if(e.isShiftDown()){
            return InputModifier.SHIFT;
        }

        return null;
    }

    /**
     * @param x1 the x-coord of the first point
     * @param y1 the y-coord of the first point
     * @param x2 the x-coord of the second point
     * @param y2 the y-corrd of the second point
     *
     * @return the distance between the two points
     */
    private double distance(double x1, double y1, double x2, double y2){
        double dx = x2 - x1;
        double dy = y2 - y1;

        return Math.sqrt(dx*dx + dy*dy);
    }


    ////////////////////////////////////////////////////////
    ///////////////// Listeners ////////////////////////////
    ////////////////////////////////////////////////////////

    public void setOnLeftDrag(Consumer<MouseDragInput> onLeftDrag) {
        this.onLeftDrag = onLeftDrag;
    }

    public void setOnRightDrag(Consumer<MouseDragInput> onRightDrag) {
        this.onRightDrag = onRightDrag;
    }

    public void setOnMiddleDrag(Consumer<MouseDragInput> onMiddleDrag) {
        this.onMiddleDrag = onMiddleDrag;
    }

    public void setOnScroll(Consumer<MouseScrollInput> onScroll) {
        this.onScroll = onScroll;
    }

    public void setOnLeftPress(Consumer<MouseInput> onLeftPress) {
        this.onLeftPress = onLeftPress;
    }

    public void setOnRightPress(Consumer<MouseInput> onRightPress) {
        this.onRightPress = onRightPress;
    }

    public void setOnMiddlePress(Consumer<MouseInput> onMiddlePress) {
        this.onMiddlePress = onMiddlePress;
    }

    public void setOnRightRelease(Consumer<MouseInput> onRightRelease) {
        this.onRightRelease = onRightRelease;
    }

    public void setOnMiddleRelease(Consumer<MouseInput> onMiddleRelease) {
        this.onMiddleRelease = onMiddleRelease;
    }

    public void setOnLeftRelease(Consumer<MouseInput> onLeftRelease) {
        this.onLeftRelease = onLeftRelease;
    }

    public void setOnRightClick(Consumer<MouseInput> onRightClick) {
        this.onRightClick = onRightClick;
    }

    public void setOnLeftClick(Consumer<MouseInput> onLeftClick) {
        this.onLeftClick = onLeftClick;
    }

    public void setOnMiddleClick(Consumer<MouseInput> onMiddleClick) {
        this.onMiddleClick = onMiddleClick;
    }

    public void setOnMouseMove(Consumer<MouseInput> onMouseMove) {
        this.onMouseMove = onMouseMove;
    }

    private enum InputModifier {
        CTRL, SHIFT
    }
}
