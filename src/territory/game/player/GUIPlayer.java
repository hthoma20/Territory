package territory.game.player;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import territory.game.Game;
import territory.game.GameColor;
import territory.game.GameState;
import territory.game.action.player.CreatePostAction;
import territory.game.action.player.CreateVillageAction;
import territory.game.action.player.CreateWallAction;
import territory.game.action.player.TrainSoldiersAction;
import territory.game.info.GameInfo;
import territory.game.info.GameOverInfo;
import territory.game.info.LostUnitInfo;
import territory.game.info.PlayerSetupInfo;
import territory.gui.Controller;
import territory.util.GlobalConstants;

import java.util.function.Consumer;

public class GUIPlayer extends Player {
    //whether we should automatically take some actions (for testing and debugging)
    private static final boolean TAKE_INITIAL_ACTIONS = GlobalConstants.TAKE_INITIAL_ACTIONS;

    private String displayName = "Un-named GUI Player";

    private Controller controller;

    public GUIPlayer(Controller controller, String name){
        super(name);
        this.controller = controller;
        controller.setPlayer(this);
    }

    public GUIPlayer(Controller controller){
        this(controller, "Un-named GUI Player");
    }

    /**
     * Take initial actions
     */
    @Override
    public void setGame(Game game){
        super.setGame(game);
    }

    @Override
    public void receiveState(GameState state) {
        controller.updateDisplay(state);
    }

    @Override
    public void receiveInfo(GameInfo info){
        System.out.println(info);

        //the game is setup so take initial actions
        if(info instanceof PlayerSetupInfo){
            controller.initImages(((PlayerSetupInfo) info).getColor());
            takeInitialActions();
        }
        else if(info instanceof LostUnitInfo){
            controller.lostUnit(((LostUnitInfo) info).getUnitIndex());
        }
        else if(info instanceof GameOverInfo){
            controller.gameOver(((GameOverInfo) info).getWinningColor());
        }
    }

    private void takeInitialActions(){
        //only have the first player take actions
        if(color != GameColor.values()[0] || !TAKE_INITIAL_ACTIONS){
            return;
        }

        takeAction(new CreateVillageAction(color, -100, 100));
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
