package territory.game.player;

import territory.game.GameState;
import territory.game.action.player.*;
import territory.game.info.GameInfo;
import territory.game.info.PlayerSetupInfo;
import territory.game.target.BuildType;
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
        int numMiners = 0;

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

        takeAction(new CreatePostAction(color, 200, -100));
        takeAction(new CreatePostAction(color, 300, -100));
        takeAction(new CreateWallAction(color, 0, 1));

        int numBuilders = 2;
        takeAction(new TrainBuildersAction(color, 0, numBuilders));
        for(int i = 0; i < numBuilders/2; i++){
            takeAction(new DirectBuilderAction(color, i, 0, BuildType.POST));
        }
        for(int i = numBuilders/2; i < numBuilders; i++){
            takeAction(new DirectBuilderAction(color, i, 1, BuildType.POST));
        }
    }
}
