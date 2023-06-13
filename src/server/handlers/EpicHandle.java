package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskmanagers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static server.handlers.WriteResponse.writeResponse;

public class EpicHandle implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public EpicHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method){
            case "GET":
                getEpic(exchange);
                break;
            case "POST":
                addOrUpdateEpic(exchange);
                break;
            case "DELETE":
                deleteEpic(exchange);
                break;
            default:
                writeResponse(exchange, "Такой операции не существует", 404);
        }
    }

    public void getEpic(HttpExchange exchange) throws IOException{

        String response;
        if(exchange.getRequestURI().getQuery() == null){
            response = gson.toJson(taskManager.getTasks());
            writeResponse(exchange, response, 200);
        }

        if(getEpicId(exchange).isEmpty()){
            writeResponse(exchange, "Некорректный идентификатор!", 404);
            return;
        }

        int id = getEpicId(exchange).get();
        if(taskManager.getEpics().contains(taskManager.getEpicById(id))){
            response = gson.toJson(taskManager.getTaskById(id));
            writeResponse(exchange, response, 200);
        } else {
            writeResponse(exchange, "Задач с таким ID не найдена ", 404);
        }
    }

    public void addOrUpdateEpic(HttpExchange exchange) throws IOException{

        InputStream json = exchange.getRequestBody();
        String jsonEpic = new String(json.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(jsonEpic, Epic.class);

        if(epic == null){
            writeResponse(exchange, "Задача не должна быть пустой!", 404);
        }

        if(taskManager.getEpics().contains(epic)){
            taskManager.updateEpic(epic);
            writeResponse(exchange, "Эпик обновлен!", 200);
        } else {
            taskManager.createEpic(epic);
            writeResponse(exchange, "Эпик создан!", 200);
        }
    }

    public void deleteEpic(HttpExchange exchange) throws IOException {

        if (exchange.getRequestURI().getQuery() == null){
            taskManager.deleteTasks();
            writeResponse(exchange, "Задачи успешно удалены!", 200);
            return;
        }

        if(getEpicId(exchange).isEmpty()){
            return;
        }

        int id = getEpicId(exchange).get();
        if (taskManager.getEpicById(id) == null) {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        taskManager.deleteEpic(id);
        writeResponse(exchange, "Задача успешно удалена!", 200);
    }


    private Optional<Integer> getEpicId(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getQuery().split("=");
        try {
            return Optional.of(Integer.parseInt(parts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

}
