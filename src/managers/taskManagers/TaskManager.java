package managers.taskManagers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.*;

public interface TaskManager {
    int createTask(Task task) throws IOException;
    void updateTask(Task task);
    void deleteTasks();
    void deleteTask(int id);
    Task getTaskById(int id);
    List<Task> getTasks();
    int createEpic(Epic epic) throws IOException;
    void updateEpic(Epic epic);
    void deleteEpics();
    void deleteEpic(int id);
    Epic getEpicById(int id);
    List<Epic> getEpics();
    int createSubtask(Subtask subtask) throws IOException;
    void updateSubtask(Subtask subtask);
    void deleteSubtasks();
    void deleteSubtaskById(int id);
    Subtask getSubtaskById(int id);
    List<Subtask> getSubtasks();
    List<Subtask> getEpicSubtasks(int epicId);
    List<Task> getHistory();
}
