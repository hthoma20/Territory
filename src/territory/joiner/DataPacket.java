package territory.joiner;

import java.io.Serializable;

public class DataPacket implements Serializable {
    int num;
    String message;

    public DataPacket(int num, String message){
        this.num = num;
        this.message = message;
    }
}
