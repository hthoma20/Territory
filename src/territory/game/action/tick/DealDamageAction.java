package territory.game.action.tick;

import territory.game.GameColor;
import territory.game.unit.Unit;

public class DealDamageAction extends TickAction {
    private Unit target;
    private int damage;

    public DealDamageAction(GameColor color, Unit target, int damage) {
        super(color);
        this.target = target;
        this.damage = damage;
    }

    public Unit getTarget() {
        return target;
    }

    public int getDamage() {
        return damage;
    }
}
