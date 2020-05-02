package territory.game.player;

import territory.game.GameState;
import territory.game.action.player.CreateVillageAction;
import territory.game.action.player.TrainMinersAction;
import territory.game.info.GameInfo;
import territory.game.info.PlayerSetupInfo;

public class ComputerPlayer extends Player {
    @Override
    protected void receiveState(GameState state) {

    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if(info instanceof PlayerSetupInfo){
            takeAction(new CreateVillageAction(color, -50, 50));
            takeAction(new TrainMinersAction(color, 0, 2));
        }
    }
}
