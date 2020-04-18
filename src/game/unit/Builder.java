package game.unit;

import game.Copyable;
import game.action.TickAction;
import game.construction.Buildable;
import game.player.Player;

import java.util.List;

public class Builder extends Unit {

    private Buildable target;

    public Builder(Player owner, double x, double y) {
        super(owner, x, y);
    }

    public Builder(Unit src) {
        super(src);

        this.target = target;
    }

    @Override
    public Builder copy(){
        return new Builder(this);
    }

    public static int getGoldPrice(){
        return 10;
    }

    public void setTarget(Buildable target){
        this.target = target;
    }

    @Override
    protected Target getTarget(){
        return this.target;
    }

    @Override
    protected List<TickAction> atTarget() {
        return null;
    }
}
