package managers;

import managers.historyManagers.HistoryManager;
import managers.historyManagers.InMemoryHistoryManager;
import managers.taskManagers.InMemoryTaskManager;
import managers.taskManagers.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
