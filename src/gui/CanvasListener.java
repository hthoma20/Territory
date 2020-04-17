package gui;

import game.player.Player;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class CanvasListener implements EventHandler<MouseEvent> {
    private Player player;

    public CanvasListener(Player player){
        this.player = player;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getX() +" " + mouseEvent.getY());
    }
}
