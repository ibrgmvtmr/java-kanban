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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static tasks.enums.TaskType.EPIC;
import static tasks.enums.TaskType.SUBTASK;

public class FileBackedTasksManager extends InMemoryTaskManager{

    private final Path filePath;

    public FileBackedTasksManager(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Task createTask(Task task){
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }
    @Override
    public Epic createEpic(Epic epic){
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }
    @Override
    public Subtask createSubtask(Subtask subtask){
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
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
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
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

            String values = "id,type,name,status,description,startTime,duration,endTime,epic" + "\n" + toString(this) + "\n" + historyToString(historyManager);
            bw.write(values);

        } catch (IOException e) {

            throw new RuntimeException();

        }
    }

    public static FileBackedTasksManager loadFromFile(Path filePath) {
        FileBackedTasksManager fileBackedTasksManager = Managers.getDefaultFileBackedTasksManager(filePath);

        try {

            String fileName = Files.readString(filePath);

            String[] lines = fileName.split("\n");

            List<Integer> history = historyFromString(lines[lines.length-1]);

            for (int i = 1; i < lines.length-2; i++) {

                Task task = fromString(lines[i]);
                String[] type = lines[i].split(",");

                if (task.getId() > fileBackedTasksManager.generatedId) {
                    fileBackedTasksManager.generatedId = task.getId();
                }

                if (TaskType.valueOf(type[1]).equals(TaskType.TASK)) {
                    fileBackedTasksManager.tasks.put(task.getId(), task);
                    if (history.contains(task.getId())) {
                        fileBackedTasksManager.historyManager.add(task);
                    }
                }

                if (TaskType.valueOf(type[1]).equals(EPIC)) {
                    Epic epic = (Epic) task;
                    task.setTaskType(EPIC);
                    fileBackedTasksManager.epics.put(epic.getId(),epic);

                    if(history.contains(epic.getId())){
                        fileBackedTasksManager.historyManager.add(epic);
                    }
                }

                if (TaskType.valueOf(type[1]).equals(TaskType.SUBTASK)) {
                    Subtask subtask = (Subtask) task;
                    task.setTaskType(SUBTASK);
                    fileBackedTasksManager.subtasks.put(subtask.getId(), subtask);
                    fileBackedTasksManager.calculateEpicEndTime(subtask.getEpicId());

                    if(history.contains(subtask.getId())){
                        fileBackedTasksManager.historyManager.add(subtask);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }

        return fileBackedTasksManager;

    }


     public static String toString(TaskManager tasksManager) {

        List<Task> allTasks = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        allTasks.addAll(tasksManager.getTasks());
        allTasks.addAll(tasksManager.getEpics());
        allTasks.addAll(tasksManager.getSubtasks());

        for (Task task : allTasks)
            result.append(task.toString()).append("\n");

        return result.toString();

    }

    public static Task fromString(String value){

        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];
        Instant startTime = Instant.parse(split[5]);
        long duration = Long.parseLong(split[6]);

        int epicId = 0;
        Instant endTime = null;

        if(TaskType.valueOf(type).equals(TaskType.SUBTASK)) {
            epicId = Integer.parseInt(split[8]);
        }

        if (split.length > 7 && !split[7].isEmpty()) {
            endTime = Instant.parse(split[7]);
        }

        if(TaskType.valueOf(type).equals(TaskType.TASK)) {
            return new Task(id, name, status, description, startTime, duration);
        }

        if(TaskType.valueOf(type).equals(EPIC)) {
            Epic epic = new Epic(id, name, status, description, startTime, duration);
            epic.setEndTime(endTime);
            return epic;
        }

        if(TaskType.valueOf(type).equals((TaskType.SUBTASK))) {
            return new Subtask(id, name, status, description, startTime, duration, epicId);
        }

        throw new IllegalArgumentException("Данный формат задачи не поддерживается");
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();

        for(Task task: manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }

        return sb.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();

        for (String line : value.split(",")) {
            history.add(Integer.parseInt(line.trim()));
        }

        Collections.sort(history);

        return history;
    }
}
