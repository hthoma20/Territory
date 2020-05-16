package territory.game.target;

import territory.game.Copyable;
import territory.game.construction.Wall;
import territory.game.unit.Builder;

import java.io.Serializable;
import java.util.*;

public class BuildProject implements Copyable<BuildProject>, Serializable {
    private List<Buildable> buildables;

    //map of builders assigned to this project, and where they should be building
    private Map<Builder, BuildSlot> builders = new HashMap<>();

    public BuildProject(Wall wall){
        this.buildables = Arrays.asList(wall.getSegments());
    }

    public BuildProject(BuildProject src){
        this.buildables = new ArrayList<>(src.buildables);
        this.builders = new HashMap<>(src.builders);
    }

    @Override
    public BuildProject copy() {
        return new BuildProject(this);
    }

    /**
     * Initialize a project with a single buildable
     * @param buildable the buildable to build
     */
    public BuildProject(Buildable buildable){
        this.buildables = new ArrayList<>();
        buildables.add(buildable);
    }

    private BuildSlot bestSlot(){
        BuildSlot bestSlot = null;

        for(Buildable buildable : buildables){
            if(buildable.isComplete()){
                continue;
            }

            BuildSlot slot = buildable.getOpenBuildSlot();
            if(bestSlot == null || slot.getUnitCount() < bestSlot.getUnitCount()){
                bestSlot = slot;
            }
        }

        return bestSlot;
    }

    public void addBuilder(Builder builder){
        BuildSlot bestSlot = bestSlot();

        if(bestSlot == null){
            return;
        }

        builders.put(builder, bestSlot);
        bestSlot.incUnitCount();
    }

    public void removeBuilder(Builder builder){
        BuildSlot slot = builders.get(builder);

        if(slot != null){
            slot.decUnitCount();
        }

        builders.remove(builder);
    }

    public Target getBuildSlot(Builder builder){
        BuildSlot slot = builders.get(builder);

        if(slot == null){
            addBuilder(builder);
        }
        else if(slot.getBuildable().isComplete()){
            removeBuilder(builder);
            addBuilder(builder);
        }

        return builders.get(builder);
    }

    public Buildable getBuildable(Builder builder){
        return builders.get(builder).getBuildable();
    }

}
