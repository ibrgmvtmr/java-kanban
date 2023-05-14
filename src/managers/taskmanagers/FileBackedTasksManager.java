package managers.taskmanagers;


import managers.Managers;
import managers.historymanagers.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;
import tasks.enums.TaskType;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static tasks.enums.TaskType.EPIC;
import static tasks.enums.TaskType.SUBTASK;


public class FileBackedTasksManager extends InMemoryTaskManager{

    private final Path filePath;

    public FileBackedTasksManager(Path filePath) {
        historyManager = Managers.getDefaultHistory();
        this.filePath = filePath;
    }

    @Override
    public int createTask(Task task) throws IOException {
        int taskId = super.createTask(task);
        save();
        return  taskId;
    }
    @Override
    public int createEpic(Epic epic) throws IOException {
        int epicId = super.createEpic(epic);
        save();
        return  epicId;
    }
    @Override
    public int createSubtask(Subtask subtask) throws IOException {
        int subtaskId = super.createSubtask(subtask);
        save();
        return  subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics(){
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }



    protected void save() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toFile()))) {

            String values = "id,type,name,status,description,epic" + "\n" + toString(this) + "\n" + historyToString(historyManager);
            bw.write(values);

        } catch (IOException e) {

            throw new RuntimeException();

        }
    }

    public static FileBackedTasksManager loadFromFile(Path filePath) {
        FileBackedTasksManager fileBackedTasksManager = Managers.getDefaultFileBackedTasksManager(filePath);

        int initialID = 0;

        List<Integer> tasksId = new ArrayList<>();
        try {

            String fileName = Files.readString(filePath);

            String[] lines = fileName.split("\n");
            String[] a = lines[8].split(",");

            for (String str : a) {
                tasksId.add(Integer.parseInt(str));
            }

            for (int i = 1; i < lines.length-2; i++) {

                Task task = fileBackedTasksManager.fromString(lines[i]);
                String[] type = lines[i].split(",");

                if (task.getId() > initialID) {
                    initialID = task.getId();
                    fileBackedTasksManager.generatedId = initialID;
                }

                if (TaskType.valueOf(type[1]).equals(TaskType.TASK)) {
                    fileBackedTasksManager.tasks.put(task.getId(), task);
                    if (tasksId.contains(task.getId())) {
                        fileBackedTasksManager.historyManager.add(task);
                    }
                }

                if (TaskType.valueOf(type[1]).equals(EPIC)) {
                    Epic epic = (Epic) task;
                    task.setTaskType(EPIC);
                    fileBackedTasksManager.epics.put(epic.getId(),epic);

                    if(tasksId.contains(epic.getId())){
                        fileBackedTasksManager.historyManager.add(epic);
                    }
                }

                if (TaskType.valueOf(type[1]).equals(TaskType.SUBTASK)) {
                    Subtask subtask = (Subtask) task;
                    task.setTaskType(SUBTASK);
                    fileBackedTasksManager.subtasks.put(subtask.getId(), subtask);
                    if(tasksId.contains(subtask.getId())){
                        fileBackedTasksManager.historyManager.add(subtask);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }

        return fileBackedTasksManager;

    }


     private String toString(TaskManager tasksManager) {

        List<Task> allTasks = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        allTasks.addAll(tasksManager.getTasks());
        allTasks.addAll(tasksManager.getEpics());
        allTasks.addAll(tasksManager.getSubtasks());

        for (Task task : allTasks)
            result.append(task.toString()).append("\n");

        return result.toString();

    }

    private Task fromString(String value){
        int epicId = 0;
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];

        if(TaskType.valueOf(type).equals(TaskType.SUBTASK)) {
            epicId = Integer.parseInt(split[5]);
        }

        if(TaskType.valueOf(type).equals(TaskType.TASK)) {
            return new Task(id, name, status, description);
        }

        if(TaskType.valueOf(type).equals(EPIC)) {
            return new Epic(id, name, status, description);
        }

        if(TaskType.valueOf(type).equals((TaskType.SUBTASK))) {
            return new Subtask(id, name, status, description, epicId);
        }

        throw new IllegalArgumentException("Данный формат задачи не поддерживается");
    }

    private String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();

        for(Task task: manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }

        return sb.toString();
    }

    private List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();

        for (String line : value.split(",")) {
            history.add(Integer.parseInt(line));
        }
        return history;
    }

    public static void main(String[] args) throws IOException {
    
        FileBackedTasksManager taskManager = Managers.getDefaultFileBackedTasksManager(Path.of("src/results.csv"));
        taskManager.save();

        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        int taskId1 = taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        int taskId2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
        int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", TaskStatus.NEW, epicId1);
        int subtaskId1 = taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description 2", TaskStatus.NEW, epicId1);
        int subtaskId2 = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask 3", "Subtask description 3", TaskStatus.NEW, epicId1);
        int subtaskId3 = taskManager.createSubtask(subtask3);

        taskManager.getEpicById(epicId1);
        taskManager.getSubtaskById(subtaskId1);
        taskManager.getSubtaskById(subtaskId2);
        taskManager.getSubtaskById(subtaskId3);

        FileBackedTasksManager taskManagerF = FileBackedTasksManager.loadFromFile(Path.of("src/results.csv"));
        System.out.println(taskManagerF.getTasks());
        System.out.println(taskManagerF.getEpics());
        System.out.println(taskManagerF.getSubtasks());
        System.out.println(taskManagerF.getHistory());

    }
}
