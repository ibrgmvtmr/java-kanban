import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();
            System.out.println("Сервер запущен на порту " + 8080);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}