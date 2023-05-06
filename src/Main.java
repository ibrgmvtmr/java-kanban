import managers.Managers;
import managers.taskManagers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        int taskId1 = taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        int taskId2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic description 1", Status.NEW);
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", Status.NEW, epicId1);
        int subtaskId1 = taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description 2", Status.NEW, epicId1);
        int subtaskId2 = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask 3", "Subtask description 3", Status.NEW, epicId1);
        int subtaskId3 = taskManager.createSubtask(subtask3);


        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId2);
        taskManager.getEpicById(epicId1);
        taskManager.getSubtaskById(subtaskId1);
        taskManager.getSubtaskById(subtaskId2);
        taskManager.getSubtaskById(subtaskId3);
        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId2);

        taskManager.deleteTask(taskId1);
        taskManager.getHistory();

        taskManager.deleteEpic(epicId1);
        taskManager.getHistory();

    }
}
