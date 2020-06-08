package territory.joiner.request;

import java.io.Serializable;

public class JoinRoomRequest implements Serializable {
    private int roomId;
    private String playerName;

    public JoinRoomRequest(int roomId, String playerName) {
        this.roomId = roomId;
        this.playerName = playerName;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getPlayerName() {
        return playerName;
    }
}
