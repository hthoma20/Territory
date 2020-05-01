package territory.joiner;


import com.sun.net.httpserver.HttpExchange;
import territory.game.GameState;

import java.io.*;

public class GameJoiner {

    public Object handleGetGameRequest() {
        return new DataPacket(100, "hellO");
    }



}
