package territory.game;

import java.io.Serializable;
import java.util.*;

public class TerritoryList implements Iterable<Territory>, Serializable {
    private List<Territory> territories = new ArrayList<>();

    public TerritoryList(){}


    public TerritoryList(List<Territory> territories){
        this.territories = territories;
    }

    public TerritoryList(Territory... territories){
        this.territories = Arrays.asList(territories);
    }

    public List<Territory> getTerritories() {
        return territories;
    }

    public void append(TerritoryList list){
        this.territories.addAll(list.territories);
    }

    public void add(Territory territory){
        this.territories.add(territory);
    }

    public double area(){
        double area = 0;
        for(Territory territory : territories){
            area += territory.area();
        }

        return area;
    }

    @Override
    public Iterator<Territory> iterator() {
        return territories.iterator();
    }
}
