package territory.game.action.player;

import territory.game.GameColor;
import territory.game.player.Player;

import java.io.Serializable;

public class CreateWallAction extends PlayerAction implements Serializable {
    private int post1Index, post2Index;

    public CreateWallAction(GameColor color, int post1Index, int post2Index) {
        super(color);
        this.post1Index = post1Index;
        this.post2Index = post2Index;
    }

    public int getPost1Index() {
        return post1Index;
    }

    public int getPost2Index() {
        return post2Index;
    }
}
