package territory.game.info;

import java.io.Serializable;

/**
 * Info to synch-up a player's name
 */
public class NameInfo implements Serializable {
    private String name;

    public NameInfo(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
