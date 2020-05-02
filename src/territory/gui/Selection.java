package territory.gui;

import territory.game.Indexable;
import territory.game.construction.Post;
import territory.game.construction.Village;
import territory.game.sprite.Sprite;
import territory.game.unit.Unit;

import java.util.HashSet;
import java.util.Set;

public class Selection {

    private Type selectionType = Type.NONE;
    private Set<Integer> selectedIndices = new HashSet<>();

    /**
     * Add the given object to the selection, change the type of selection accordingly
     * @param object the object to select
     * @throws RuntimeException if this is not an selectable object
     */
    public void select(Indexable object){
        if(object instanceof Village){
            selectionType = Type.VILLAGE;
            selectedIndices.clear();
        }
        else if(object instanceof Post){
            selectionType = Type.POST;
            selectedIndices.clear();
        }
        else if(object instanceof Unit){
            if(selectionType != Type.UNITS){
                selectedIndices.clear();
            }
            selectionType = Type.UNITS;
        }

        selectedIndices.add(object.getIndex());
    }

    public void clear(){
        selectedIndices.clear();
        selectionType = Type.NONE;
    }

    /**
     * Determine whether the given object is selected
     * @param object the object to check if it is selected
     * @return whether the given object is selected
     */
    public boolean contains(Indexable object){
        //check that the type is correct
        switch(selectionType){
            case NONE:
                return false;
            case VILLAGE:
                if( !(object instanceof Village)){
                    return false;
                }
                break;
            case POST:
                if( !(object instanceof Post)){
                    return false;
                }
                break;
            case UNITS:
                if( !(object instanceof Unit)){
                    return false;
                }
                break;
        }

        //check whether the index is selected
        return selectedIndices.contains(object.getIndex());
    }

    public void setType(Type type){
        this.selectionType = type;
    }

    public Type getType(){
        return selectionType;
    }

    public Set<Integer> getIndices(){
        return selectedIndices;
    }

    /**
     * Called to indicate that a unit was lost
     * @param index the index of the lost unit
     */
    public void lostUnit(int index){

    }

    /**
     * @return a single selected index
     * @throws RuntimeException if there is more than one selected index
     */
    public int getIndex(){
        if(selectedIndices.size() != 1){
            throw new RuntimeException(String.format("There are %d indices selected.", selectedIndices.size()));
        }

        return selectedIndices.iterator().next();
    }

    public enum Type {
        NONE, VILLAGE, POST, UNITS
    }
}
