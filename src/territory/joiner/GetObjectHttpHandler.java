package territory.joiner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

/**
 * Class that handles an http exchange by returning a serialized object
 */
public class GetObjectHttpHandler implements HttpHandler {

    private Supplier<Object> objectSupplier;

    public GetObjectHttpHandler(Supplier<Object> objectSupplier){
        this.objectSupplier = objectSupplier;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Handling");
        OutputStream out = httpExchange.getResponseBody();

        Object responseObject = objectSupplier.get();
        byte[] response = getBytes(responseObject);

        httpExchange.sendResponseHeaders(200, response.length);

        out.write(response);
        out.close();
    }

    /**
     * Serialize the object and return the bytes
     * @param obj the object to get bytes for
     * @return the bytes of the serial representation
     */
    public byte[] getBytes(Object obj){

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
