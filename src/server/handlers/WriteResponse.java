package server.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class WriteResponse {

    public static void writeResponse(HttpExchange exchange,String response, int responseCode) throws IOException {
        if(response.isBlank()){
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()){
                os.write(bytes);
            }
        }
        exchange.close();
    }
}
