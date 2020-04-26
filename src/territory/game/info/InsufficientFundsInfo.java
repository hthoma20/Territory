package territory.game.info;

import java.io.Serializable;

public class InsufficientFundsInfo extends GameInfo implements Serializable {
    public InsufficientFundsInfo() {
        super("Insufficient funds");
    }
}
