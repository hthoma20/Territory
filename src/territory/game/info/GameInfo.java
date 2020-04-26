package territory.game.info;

public class GameInfo {
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
