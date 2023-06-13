package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskmanagers.TaskManager;

import java.io.IOException;
import java.util.Optional;

import static server.handlers.WriteResponse.writeResponse;

public class SubtasksForEpicHandle implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public SubtasksForEpicHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getEpicSubtasks(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);
        }
    }

    private void getEpicSubtasks(HttpExchange exchange) throws IOException {
        String response;
        if (getTaskId(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", 404);
            return;
        }

        int id = getTaskId(exchange).get();
        if (taskManager.getEpicSubtasks(id) != null) {
            response = gson.toJson(taskManager.getEpicSubtasks(id));
            writeResponse(exchange, response, 200);
        } else {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
        }
    }



    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getQuery().split("=");
        try {
            return Optional.of(Integer.parseInt(pathParts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
