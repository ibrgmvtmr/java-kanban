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

    static public TaskManager getInMemoryTaskManger() {
        return new InMemoryTaskManager();
    }

    static public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static FileBackedTasksManager getDefaultFileBackedTasksManager(Path path) {
        return new FileBackedTasksManager(path);
    }
}
