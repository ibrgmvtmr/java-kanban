import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = Managers.getDefault();



        // create a task
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        int taskId = taskManager.createTask(task);
        System.out.println("Created task with ID " + taskId);

        // update the task
        task.setDescription("Updated description");
        taskManager.updateTask(task);

        // create an epic
        Epic epic = new Epic("Epic 1", "Epic description", Status.NEW);
        int epicId = taskManager.createEpic(epic);
        System.out.println("Created epic with ID " + epicId);

        // create a subtask
        Subtask subtask = new Subtask("Subtask 1", "Subtask description", Status.NEW, epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        System.out.println("Created subtask with ID " + subtaskId);

        // update the subtask
        subtask.setDescription("Updated subtask description");
        taskManager.updateSubtask(subtask);

        // get the task
        Task retrievedTask = taskManager.getTaskById(taskId);
        System.out.println("Retrieved task: " + retrievedTask);

        // get the subtask
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtaskId);
        System.out.println("Retrieved subtask: " + retrievedSubtask);


        // get the epic
        Epic retrievedEpic = taskManager.getEpicById(epicId);
        System.out.println("Retrieved epic: " + retrievedEpic);

        // get the history of subtasks
        taskManager.getHistory();
    }
}
