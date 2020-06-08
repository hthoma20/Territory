package territory.game.player;

import territory.game.GameState;
import territory.game.action.player.CreateVillageAction;
import territory.game.action.player.DirectSoldierAction;
import territory.game.action.player.TrainMinersAction;
import territory.game.action.player.TrainSoldiersAction;
import territory.game.info.GameInfo;
import territory.game.info.PlayerSetupInfo;
import territory.game.target.PatrolArea;

public class ComputerPlayer extends Player {
    @Override
    protected void receiveState(GameState state) {

    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if(info instanceof PlayerSetupInfo){
            takeInitialActions();
        }
    }

    private void takeInitialActions(){

        takeAction(new CreateVillageAction(color, 0, 0));

        int numSoldiers = 2;

        takeAction(new TrainSoldiersAction(color, 0, numSoldiers));

        PatrolArea patrolArea = new PatrolArea(color, 0, 0, 700);

        for(int i = 0; i < numSoldiers; i++){
            takeAction(new DirectSoldierAction(color, i, patrolArea));
        }
    }
}
