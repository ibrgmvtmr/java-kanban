package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskmanagers.TaskManager;

import java.io.IOException;

import static server.handlers.WriteResponse.writeResponse;

public class PriorityHandle implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public PriorityHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method){
            case "GET":
                getPriorityTask(exchange);
                break;
            default:
                writeResponse(exchange, "Такой операции не существует", 404);
        }
    }

    private void getPriorityTask(HttpExchange exchange) throws IOException {
        if(taskManager.getPrioritizedTasks().isEmpty()){
            writeResponse(exchange, "Задач пока что нет :(", 200);
        } else {
            String response = gson.toJson(taskManager.getPrioritizedTasks());
            writeResponse(exchange, response, 200);
        }
    }

}
