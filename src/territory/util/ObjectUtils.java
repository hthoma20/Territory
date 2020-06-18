package territory.util;

import java.io.*;

public class ObjectUtils {

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

    /**
     * Make a transitive copy of the given object
     * @param obj the object to copy
     * @param <T> the type of the object to copy
     * @return a deep, transitive copy of the given object
     */
    public static <T> T transitiveCopy(T obj){
        byte[] bytes = getBytes(obj);

        try(ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
            ObjectInputStream objIn = new ObjectInputStream(bytesIn)){

            Object copy = objIn.readObject();
            return (T)copy;
        }
        catch(IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
