package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskmanagers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static server.handlers.WriteResponse.writeResponse;

public class SubtaskHandle implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public SubtaskHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method){
            case "GET":
                getSubtask(exchange);
                break;
            case "POST":
                addOrUpdateSubtask(exchange);
                break;
            case "DELETE":
                deleteSubtask(exchange);
                break;
            default:
                writeResponse(exchange, "Такой операции не существует", 404);
        }
    }

    public void getSubtask(HttpExchange exchange) throws IOException {
        String response;

        if(exchange.getRequestURI().getQuery() == null){
            response = gson.toJson(taskManager.getSubtasks());
            writeResponse(exchange, response, 200);
            return;
        }
        if(getSubtaskId(exchange).isEmpty()){
            writeResponse(exchange, "Некорректный идентификатор!", 404);
            return;
        }

        int id = getSubtaskId(exchange).get();
        if(taskManager.getSubtasks().contains(taskManager.getSubtaskById(id))){
            response = gson.toJson(taskManager.getSubtaskById(id));
            writeResponse(exchange, response, 200);
        } else {
            writeResponse(exchange, "Задач с таким ID не найдена ", 404);
        }
    }

    private void addOrUpdateSubtask(HttpExchange exchange) throws IOException {
        try{
            InputStream json = exchange.getRequestBody();
            String jsonSubtask = new String(json.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
            if(subtask == null) {
                writeResponse(exchange, "Задача не должна быть пустой!", 404);
                return;
            }

            if( taskManager.getSubtasks().contains(subtask)){
                taskManager.updateSubtask(subtask);
                writeResponse(exchange, "Эпик обновлен!", 218);
                return;
            }
            taskManager.createSubtask(subtask);
            writeResponse(exchange, "Задача успешно добавлена!", 200);
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 404);
        }
    }

    private void deleteSubtask(HttpExchange exchange) throws IOException {
        if(exchange.getRequestURI().getQuery() == null){
            taskManager.deleteSubtasks();
            writeResponse(exchange, "Задачи успешно удалены!", 200);
            return;
        }

        if (getSubtaskId(exchange).isEmpty()) {
            return;
        }

        int id = getSubtaskId(exchange).get();
        if (!(taskManager.getSubtasks().contains(taskManager.getSubtaskById(id)))){
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        taskManager.deleteSubtask(id);
        writeResponse(exchange, "Задача успешно удалена!", 200);
    }

    private Optional<Integer> getSubtaskId(HttpExchange httpExchange){
        String[] parts = httpExchange.getRequestURI().getQuery().split("=");
        try{
            return Optional.of(Integer.parseInt(parts[1]));
        } catch (NumberFormatException e){
            return Optional.empty();
        }
    }
}
