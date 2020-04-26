package territory.game.info;

import java.io.Serializable;

public class GameInfo implements Serializable {
    private String message;

    public GameInfo(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString(){
        return String.format("%s: %s", getClass().getSimpleName(), message);
    }
}
