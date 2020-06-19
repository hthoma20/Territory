package territory.game.construction.upgrade;

import java.io.Serializable;
import java.util.*;

public class WorkShop implements Serializable {

    //the things that can currently be built in this shop
    public Set<WorkShopItem> benches = new HashSet<>();

    //how many of each thing we have in the shop
    public Map<WorkShopItem, Integer> inventory = new HashMap<>();

    public void addBench(WorkShopItem bench){
        boolean added = this.benches.add(bench);

        //if we already had this bench, do nothing
        if(!added){
            return;
        }

        //mark that we have none of this item yet
        inventory.put(bench, 0);
    }

    public Set<WorkShopItem> getBenches() {
        return benches;
    }

    public boolean hasBench(WorkShopItem bench){
        return benches.contains(bench);
    }

    /**
     * @param item the item to get the stock of
     * @return how many of the item are in stock
     */
    public int stock(WorkShopItem item){
        return inventory.getOrDefault(item, 0);
    }

    public void addItem(WorkShopItem item){
        //incriment the stock of the item
        inventory.put(item, inventory.get(item) + 1);
    }

    /**
     * @return a list of benches that can be added to this shop
     */
    public List<WorkShopItem> availableBenches(){
        List<WorkShopItem> availableBenches = new ArrayList<>();

        for(WorkShopItem bench : WorkShopItem.values()){
            if(!hasBench(bench)){
                availableBenches.add(bench);
            }
        }

        return availableBenches;
    }
}
