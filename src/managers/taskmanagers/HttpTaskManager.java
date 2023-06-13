package managers.taskmanagers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager{

    KVTaskClient kvTaskClient;
    Gson gson = new Gson();

    public HttpTaskManager(String URL) throws IOException, InterruptedException {
        this.kvTaskClient = new KVTaskClient(URL);
        loadFromServer();
    }

    @Override
    public void save() {
        try {
            kvTaskClient.put("task", gson.toJson(super.getTasks()));
            kvTaskClient.put("epic", gson.toJson(super.getEpics()));
            kvTaskClient.put("subtask", gson.toJson(super.getSubtasks()));
            kvTaskClient.put("history", gson.toJson(super.getHistory()));
        } catch (IOException | InterruptedException e ) {
                throw new RuntimeException(e);
        }
    }

    public void  loadFromServer() {
        try {
            JsonArray loadedArray = kvTaskClient.load("task");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTask : loadedArray) {
                Task loadedTask = gson.fromJson(jsonTask, Task.class);
                int id = loadedTask.getId();
                super.tasks.put(id, loadedTask);
            }
            loadedArray = kvTaskClient.load("epic");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTask : loadedArray) {
                Epic loadedEpic = gson.fromJson(jsonTask, Epic.class);
                int id = loadedEpic.getId();
                super.epics.put(id, loadedEpic);
            }
            loadedArray = kvTaskClient.load("subtask");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTask : loadedArray) {
                Subtask loadedSubTask = gson.fromJson(jsonTask, Subtask.class);
                int id = loadedSubTask.getId();
                super.subtasks.put(id, loadedSubTask);
            }
            loadedArray = kvTaskClient.load("history");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTaskId : loadedArray) {
                if (jsonTaskId == null) {
                    break;
                }
                int loadedId = jsonTaskId.getAsInt();

                if (epics.containsKey(loadedId)) {
                    getEpicById(loadedId);
                } else if (tasks.containsKey(loadedId)) {
                    getTaskById(loadedId);
                } else if (subtasks.containsKey(loadedId)) {
                    getTaskById(loadedId);
                }
            }
        } catch (UnsupportedOperationException e) {
            System.out.println(" ");
        }

    }
}