package territory.game.info;

import java.io.Serializable;

public class IllegalWallInfo extends IllegalActionInfo implements Serializable {
    public static final IllegalWallInfo POST_NOT_BUILT = new IllegalWallInfo("Post not built");
    public static final IllegalWallInfo DUPLICATE_POST = new IllegalWallInfo("Wall cannot be built on single post");

    public IllegalWallInfo(String message){
        super(message);
    }

    public IllegalWallInfo() {
        this("Illegal wall");
    }
}
