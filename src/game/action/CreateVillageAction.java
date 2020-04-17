package game.action;

import game.player.Player;

public class CreateVillageAction extends GameAction {
    private double x, y;

    public CreateVillageAction(Player player, double x, double y) {
        super(player);
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
