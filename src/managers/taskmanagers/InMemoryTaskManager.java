package managers.taskmanagers;
import exceptions.IntersectionException;
import managers.Managers;
import managers.historymanagers.HistoryManager;
import tasks.Epic;
import tasks.enums.TaskStatus;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager,  Comparator<Task> {
    protected int generatedId = 1;

    protected HistoryManager historyManager;
    private final Set<Task> prioritizedTasks;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask>  subtasks;
    protected final HashMap<Integer, Epic> epics;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(this);
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public Task createTask(Task task){
        task.setId(generatedId++);
        task.setStatus(TaskStatus.NEW);
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Epic createEpic(Epic epic){
        epic.setId(generatedId++);
        epic.setStatus(TaskStatus.NEW);
        addToPrioritizedTasks(epic);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic storedEpic = epics.get(epic.getId());
            if (!storedEpic.getName().equals(epic.getName())) {
                storedEpic.setName(epic.getName());
            }
            if (!storedEpic.getDescription().equals(epic.getDescription())) {
                storedEpic.setDescription(epic.getDescription());
            }
        }
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                if (subtasks.containsKey(subtaskId)) {
                    prioritizedTasks.removeIf(task -> task.getId() == id);
                    subtasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                }
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Subtask createSubtask(Subtask subtask){
        subtask.setId(generatedId);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        prioritizedTasks.add(subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());

        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            if (subtasks.containsKey(subtask.getId())) {
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(epic.getId());
            }
        }
    }
    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtasks.remove(id);
            addToPrioritizedTasks(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(id);
            }
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
    }
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        List<Integer> subtaskIds = epic.getSubtaskIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
    @Override
    public void addToPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
        checkIntersections();
    }

    @Override
    public void printPrioritizedTasks() {
        System.out.println("СПИСОК ПРИОРИТЕТНЫХ ЗАДАЧ:");
        prioritizedTasks.forEach(System.out::println);
    }

    @Override
    public void checkIntersections() {
        List<Task> prioritizedTasks = getPrioritizedTasks();

        for (int i = 1; i < prioritizedTasks.size(); i++) {
            Task prioritizedTask = prioritizedTasks.get(i);
            if(prioritizedTask.getStartTime().isBefore(prioritizedTasks.get(i - 1).getEndTime())){
                throw new IntersectionException("Найдено пересечение между "
                        + prioritizedTasks.get(i)
                        + "и"
                        + prioritizedTasks.get(i -1));
            }
        }
    }

    @Override
    public int compare(Task task1, Task task2) {
        return  task1.getStartTime().compareTo(task2.getStartTime());
    }

    public void updateEpicStatus(int epicId) {
        if (!subtasks.isEmpty()) {
            Epic epic = epics.get(epicId);
            boolean allDone = true;
            boolean inProgress = false;

            Instant startTime = Instant.ofEpochSecond(0);
            Instant endTime = Instant.ofEpochSecond(0);

            if(subtasks.get(0) != null) {
                startTime = subtasks.get(0).getStartTime();
                endTime = subtasks.get(0).getEndTime();
            }


            for (Integer subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask.getStatus() != TaskStatus.DONE) {
                    allDone = false;
                }
                if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                    inProgress = true;
                }

                if (subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }

                if (subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }

                epic.setStartTime(startTime);
                epic.setEndTime(endTime);
                epic.setDuration(Duration.between(startTime, endTime).toMinutes());

            }

            if (allDone && epic.getStatus() != TaskStatus.DONE ) {
                epic.setStatus(TaskStatus.DONE);
                epics.put(epicId, epic);
            } else if (inProgress && epic.getStatus() != TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                epics.put(epicId, epic);
            } else if (!inProgress && !allDone && epic.getStatus() != TaskStatus.NEW) {
                epic.setStatus(TaskStatus.NEW);
                epics.put(epicId, epic);
            }
        }
    }
}
