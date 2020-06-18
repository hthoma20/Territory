package territory.joiner.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.function.Function;

import static territory.util.ObjectUtils.getBytes;

/**
 * Class that handles an http exchange by deserializing an object as the body,
 * are responding with a serialized object
 */
public class HttpObjectHandler implements HttpHandler {

    private Function<Object, Object> handler;

    public HttpObjectHandler(Function<Object, Object> objectSupplier){
        this.handler = objectSupplier;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream out = httpExchange.getResponseBody();
        ObjectInputStream in = null;

        try {
            in = new ObjectInputStream(httpExchange.getRequestBody());
        }
        //if there is an eof, then the client sent no data
        catch(EOFException ignored){}

        Object requestObject = null;
        if(in != null) {
            try {
                requestObject = in.readObject();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Object responseObject = handler.apply(requestObject);
        byte[] response = getBytes(responseObject);

        httpExchange.sendResponseHeaders(200, response.length);

        out.write(response);
        out.close();
    }
}
