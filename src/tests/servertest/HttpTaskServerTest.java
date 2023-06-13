package tests.servertest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import managers.taskmanagers.HttpTaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {

    private static final KVServer kvServer;
    private static HttpTaskServer httpServer;

    private static Gson gson;

    static  {
        try {
            kvServer = new KVServer();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        kvServer.start();
        httpServer = new HttpTaskServer();
        httpServer.start();
        gson = new Gson();
    }

    @AfterAll
    static void shutDown() {
        httpServer.stop();
        kvServer.stop();
    }

    protected Task newTask() {
        return new Task("Task1", "Description of Task1", Instant.EPOCH, 0);
    }

    protected Epic newEpic() {
        return new Epic("Epic1", "Description of Epic1", TaskType.EPIC);
    }

    protected Subtask newSubtask(Epic epic) {
        return new Subtask("Subtask1", "Description of Subtask1", Instant.EPOCH, 0, epic.getId());
    }


    @Test
    public void correctloadFromKVServer() throws IOException, InterruptedException {
        HttpTaskManager taskManager = new HttpTaskManager("http://localhost:"+KVServer.PORT);

        Task task1 = newTask();

        taskManager.createTask(task1);

        int sizeMapTakBeforeLoad = taskManager.getTasks().size();
        int sizeMapEpicBeforeLoad = taskManager.getEpics().size();

        System.out.println("Epics " + sizeMapEpicBeforeLoad + " Tasks " + sizeMapTakBeforeLoad);

        HttpTaskManager taskManagerLoad = new HttpTaskManager("http://localhost:8078");

        int sizeMapTakAfterLoad = taskManagerLoad.getTasks().size();
        int sizeMapEpicAfterLoad = taskManagerLoad.getEpics().size();

        assertEquals(sizeMapTakBeforeLoad, sizeMapTakAfterLoad, "Загрузка Задач прошла неудачно!");
        assertEquals(sizeMapEpicBeforeLoad, sizeMapEpicAfterLoad, "Загрузка Эпиков прошла неудачно!");
    }

    @Test
    void addTasksToTaskServerOrUpdate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = newEpic();
        Subtask subtask = newSubtask(epic);
        Task task = newTask();

        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(epic);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());

        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        url = URI.create("http://localhost:8080/tasks/task/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(0, arrayTasks.size());

    }
}
