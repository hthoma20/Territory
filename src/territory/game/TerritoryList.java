package territory.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TerritoryList implements Copyable<TerritoryList>, Iterable<Territory>, Serializable {
    private List<Territory> territories = new ArrayList<>();

    public TerritoryList(){}


    public TerritoryList(List<Territory> territories){
        this.territories = territories;
    }

    public TerritoryList(Territory... territories){
        this.territories = Arrays.asList(territories);
    }

    public TerritoryList(TerritoryList src){
        this.territories = new ArrayList<>(src.territories.size());

        for(Territory territory : src.territories){
            this.territories.add(territory.copy());
        }
    }

    @Override
    public TerritoryList copy(){
        return new TerritoryList(this);
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

    @Override
    public Iterator<Territory> iterator() {
        return territories.iterator();
    }
}
