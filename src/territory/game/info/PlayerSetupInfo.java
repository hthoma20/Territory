package territory.game.info;

import territory.game.GameColor;

import java.io.Serializable;

public class PlayerSetupInfo extends GameInfo implements Serializable {
    private int index;
    private GameColor color;

    public PlayerSetupInfo(int index, GameColor color){
        super(String.format("Setup player: index %d, color %s", index, color));

        this.index = index;
        this.color = color;
    }

    public int getIndex() {
        return index;
    }

    public GameColor getColor() {
        return color;
    }
}
