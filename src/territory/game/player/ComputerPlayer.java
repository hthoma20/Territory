package territory.game.player;

import territory.game.GameState;
import territory.game.action.player.*;
import territory.game.info.GameInfo;
import territory.game.info.PlayerSetupInfo;
import territory.game.target.PatrolArea;

import java.util.HashSet;
import java.util.Set;

public class ComputerPlayer extends Player {

    public ComputerPlayer(){
        super(Player.randomName());
    }

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

        int numSoldiers = 0;
        int numMiners = 5;

        takeAction(new TrainSoldiersAction(color, 0, numSoldiers));

        PatrolArea patrolArea = new PatrolArea(color, -20, 100, 100);

        Set<Integer> soldierIndices = new HashSet<>();
        for(int i = 0; i < numSoldiers; i++){
            soldierIndices.add(i);
        }

        if(soldierIndices.size() > 0) {
            takeAction(new DirectSoldiersAction(color, soldierIndices, patrolArea));
        }


        takeAction(new TrainMinersAction(color, 0, numMiners));


    }
}
