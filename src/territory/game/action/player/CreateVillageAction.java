package territory.game.action.player;

import territory.game.GameColor;
import territory.game.player.Player;

import java.io.Serializable;

public class CreateVillageAction extends PlayerAction implements Serializable {
    private double x, y;

    public CreateVillageAction(GameColor color, double x, double y) {
        super(color);
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
