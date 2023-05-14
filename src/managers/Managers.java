package managers;

import managers.historymanagers.HistoryManager;
import managers.historymanagers.InMemoryHistoryManager;
import managers.taskmanagers.FileBackedTasksManager;
import managers.taskmanagers.InMemoryTaskManager;
import managers.taskmanagers.TaskManager;

import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static FileBackedTasksManager getDefaultFileBackedTasksManager(Path path) {
        return new FileBackedTasksManager(path);
    }
}
