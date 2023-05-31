package managers.taskmanagers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public interface TaskManager {
    Task createTask(Task task);
    void updateTask(Task task);
    void deleteTasks();
    void deleteTask(int id);
    Task getTaskById(int id);
    List<Task> getTasks();
    Epic createEpic(Epic epic);
    void updateEpic(Epic epic);
    void deleteEpics();
    void deleteEpic(int id);
    Epic getEpicById(int id);
    List<Epic> getEpics();
    Subtask createSubtask(Subtask subtask);
    void updateSubtask(Subtask subtask);
    void deleteSubtasks();
    void deleteSubtask(int id);
    Subtask getSubtaskById(int id);
    List<Subtask> getSubtasks();
    List<Subtask> getEpicSubtasks(int epicId);
    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
    void addToPrioritizedTasks(Task task);
    void checkIntersections(Task task);
    public void printPrioritizedTasks();
    void updateEpicStatus(int epicId);
}
