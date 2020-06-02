package territory.game.player;

import territory.game.Game;
import territory.game.GameColor;
import territory.game.GameState;
import territory.game.action.player.CreatePostAction;
import territory.game.action.player.CreateVillageAction;
import territory.game.action.player.CreateWallAction;
import territory.game.action.player.TrainSoldiersAction;
import territory.game.info.GameInfo;
import territory.game.info.LostUnitInfo;
import territory.game.info.PlayerSetupInfo;
import territory.gui.Controller;

public class GUIPlayer extends Player {
    private Controller controller;

    public GUIPlayer(Controller controller){
        this.controller = controller;
        controller.setPlayer(this);
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
            //takeInitialActions();
        }
        else if(info instanceof LostUnitInfo){
            controller.lostUnit(((LostUnitInfo) info).getUnitIndex());
        }
    }

    private void takeInitialActions(){

        if(color != GameColor.values()[0]){
            return;
        }

        takeAction(new CreateVillageAction(color, -100, 100));
        takeAction(new TrainSoldiersAction(color, 0, 5));

        takeAction(new CreatePostAction(color, 50, 50));
        takeAction(new CreatePostAction(color, 150,150));
    }
}
