package managers;

import managers.historymanagers.HistoryManager;
import managers.historymanagers.InMemoryHistoryManager;
import managers.taskmanagers.FileBackedTasksManager;
import managers.taskmanagers.HttpTaskManager;
import managers.taskmanagers.InMemoryTaskManager;
import managers.taskmanagers.TaskManager;

import java.io.IOException;
import java.nio.file.Path;

public class Managers {

    public static HttpTaskManager getDefault(String URL) throws IOException, InterruptedException {
        return new HttpTaskManager(URL);
    }

    public static TaskManager getInMemoryTaskManger() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static FileBackedTasksManager getDefaultFileBackedTasksManager(Path path) {
        return new FileBackedTasksManager(path);
    }
}
