package territory.joiner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectBytes {

    /**
     * Serialize the object and return the bytes
     * @param obj the object to get bytes for
     * @return the bytes of the serial representation
     */
    public static byte[] getBytes(Object obj){

        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteStream)) {

            out.writeObject(obj);
            return byteStream.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
