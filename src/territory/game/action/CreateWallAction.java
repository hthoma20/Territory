package territory.game.action;

import territory.game.player.Player;

public class CreateWallAction extends PlayerAction {
    private int post1Index, post2Index;

    public CreateWallAction(Player player, int post1Index, int post2Index) {
        super(player);
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
