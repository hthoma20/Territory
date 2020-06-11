package territory.joiner.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Reply to any request with a string and status
 */
public class HttpSimpleHandler implements HttpHandler {

    private int responseStatus;
    private String responseString;

    public HttpSimpleHandler(int responseStatus, String responseString){
        this.responseStatus = responseStatus;
        this.responseString = responseString;
    }

    public HttpSimpleHandler(String responseString){
        this(200, responseString);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream out = httpExchange.getResponseBody();

        byte[] response = responseString.getBytes();

        httpExchange.sendResponseHeaders(responseStatus, response.length);

        out.write(response);
        out.close();
    }
}
