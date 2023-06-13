package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskmanagers.TaskManager;

import java.io.IOException;

public class HistoryHandle implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public HistoryHandle(TaskManager newTaskmanager){
        this.taskManager = newTaskmanager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method){
            case "GET":
                getHistory(exchange);
                break;
            default:
                WriteResponse.writeResponse(exchange, "Такой операции не существует", 404);
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException{
        if(taskManager.getHistory().isEmpty()){
            WriteResponse.writeResponse(exchange, "История пуста!", 200);
        } else {
            String response = gson.toJson(taskManager.getHistory());
            WriteResponse.writeResponse(exchange, response, 200);
        }
    }
}
