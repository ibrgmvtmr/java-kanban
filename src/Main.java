//import managers.Managers;
//import managers.taskManagers.FileBackedTasksManager;
//import managers.taskManagers.TaskManager;
//import tasks.Epic;
//import tasks.Subtask;
//import tasks.Task;
//import tasks.enums.TaskStatus;
//
//import java.io.File;
//import java.io.IOException;
//
//public class Main {
//
//    public static void main(String[] args) throws IOException {
//
//        TaskManager taskManager = Managers.getDefault();
//
//        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
//        int taskId1 = taskManager.createTask(task1);
//
//        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
//        int taskId2 = taskManager.createTask(task2);
//
//        Epic epic1 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
//        int epicId1 = taskManager.createEpic(epic1);
//
//        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", TaskStatus.NEW, epicId1);
//        int subtaskId1 = taskManager.createSubtask(subtask1);
//
//        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description 2", TaskStatus.NEW, epicId1);
//        int subtaskId2 = taskManager.createSubtask(subtask2);
//
//        Subtask subtask3 = new Subtask("Subtask 3", "Subtask description 3", TaskStatus.NEW, epicId1);
//        int subtaskId3 = taskManager.createSubtask(subtask3);
//
//
////        taskManager.getTaskById(taskId1);
////        taskManager.getTaskById(taskId2);
////        taskManager.getEpicById(epicId1);
////        taskManager.getSubtaskById(subtaskId1);
////        taskManager.getSubtaskById(subtaskId2);
////        taskManager.getSubtaskById(subtaskId3);
////
////        taskManager.getTaskById(taskId1);
////        taskManager.getTaskById(taskId2);
////
////        System.out.println(taskManager.getHistory());
////
////        taskManager.deleteTask(taskId1);
////        System.out.println(taskManager.getHistory());
////
////        taskManager.deleteEpic(epicId1);
////        System.out.println(taskManager.getHistory());
////
////        System.out.println(taskManagerF.getHistory());
//
//        System.out.println(taskManager.getTasks());
//        System.out.println(taskManager.getEpics());
//        System.out.println(taskManager.getSubtasks());
//    }
//} 