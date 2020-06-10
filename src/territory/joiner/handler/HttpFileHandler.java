package territory.joiner.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Return a static file on request
 */
public class HttpFileHandler implements HttpHandler {

    private String filePath;

    public HttpFileHandler(String filePath){
        this.filePath = filePath;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Requesting server jar");
        OutputStream out = httpExchange.getResponseBody();

        File file = new File(filePath);
        FileInputStream in = new FileInputStream(file);

        byte[] bytes = new byte[(int)file.length()];

        int byteCount = in.read(bytes);

        httpExchange.sendResponseHeaders(200, byteCount);
        out.write(bytes);
        out.close();
    }
}
