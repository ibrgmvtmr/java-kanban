package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskmanagers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static server.handlers.WriteResponse.writeResponse;

public class TaskHandle implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public TaskHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method){
            case "GET":
                getTask(exchange);
                break;
            case "POST":
                addOrUpdateTask(exchange);
                break;
            case "DELETE":
                deleteTask(exchange);
                break;
            default:
                writeResponse(exchange, "Такой операции не существует", 404);
        }
    }
    private void getTask(HttpExchange exchange) throws IOException {
        String response;
        if(exchange.getRequestURI().getQuery() == null){
            response = gson.toJson(taskManager.getTasks());
            writeResponse(exchange, response, 200);
            return;
        }
        if(getTaskId(exchange).isEmpty()){
            writeResponse(exchange, "Некорректный идентификатор!", 404);
            return;
        }

        int id = getTaskId(exchange).get();
        if(taskManager.getTasks().contains(taskManager.getTaskById(id))){
            response = gson.toJson(taskManager.getTaskById(id));
            writeResponse(exchange, response, 200);
        } else {
            writeResponse(exchange, "Задач с таким ID не найдена ", 404);
        }
    }

    private void addOrUpdateTask(HttpExchange exchange) throws IOException {
        InputStream json = exchange.getRequestBody();

        String jsonTask = new String(json.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(jsonTask, Task.class);

        if(task == null) {
            writeResponse(exchange, "Задача не должна быть пустой!", 404);
        }

        if(taskManager.getTasks().contains(task)){
            taskManager.updateTask(task);
            writeResponse(exchange, "Задача обновлена!", 200);
        }

        taskManager.createTask(task);
        writeResponse(exchange, "Задача успешно добавлена!", 200);

    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            taskManager.deleteTasks();
            writeResponse(exchange, "Задачи успешно удалены!", 200);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            return;
        }

        int id = getTaskId(exchange).get();
        if (taskManager.getTaskById(id) == null) {
            writeResponse(exchange, "Задача с таким id не найдена!", 404);
            return;
        }
        taskManager.deleteTask(id);
        writeResponse(exchange, "Задача успешно удалена!", 200);
    }



    private Optional<Integer> getTaskId(HttpExchange httpExchange){
        String[] parts = httpExchange.getRequestURI().getQuery().split("=");
        try {
            return Optional.of(Integer.parseInt(parts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
