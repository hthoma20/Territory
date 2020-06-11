package territory.game.info;

import territory.game.GameColor;

public class GameOverInfo extends GameInfo {

    private GameColor winningColor;

    public GameOverInfo(GameColor winningColor) {
        super(String.format("The %s team won", winningColor));

        this.winningColor = winningColor;
    }

    public GameColor getWinningColor() {
        return winningColor;
    }
}
