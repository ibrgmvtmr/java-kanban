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

public class InMemoryTaskManager implements TaskManager {
    protected int generatedId = 1;

    protected HistoryManager historyManager;
    private final Set<Task> prioritizedTasks;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask>  subtasks;
    protected final HashMap<Integer, Epic> epics;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
                Comparator.nullsFirst(Comparator.naturalOrder())).thenComparing(Task::getId)
        );
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public Task createTask(Task task){
        checkIntersections(task);
        task.setId(generatedId++);
        task.setStatus(TaskStatus.NEW);
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            checkIntersections(task);
            tasks.put(task.getId(), task);
            prioritizedTasks.remove(tasks.get(task.getId()));
            addToPrioritizedTasks(task);
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
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        epics.clear();
        subtasks.clear();
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
        checkIntersections(subtask);
        subtask.setId(generatedId);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        calculateEpicEndTime(epic.getId());
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
                calculateEpicEndTime(epic.getId());
                updateEpicStatus(epic.getId());
                prioritizedTasks.remove(subtasks.get(subtask.getId()));
                checkIntersections(subtask);
                prioritizedTasks.add(subtask);
            }
        }
    }
    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtask(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
            updateEpicStatus(epic.getId());
            calculateEpicEndTime(epic.getId());
        }
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
            calculateEpicEndTime(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
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

    protected void addToPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
    }

    @Override
    public void printPrioritizedTasks() {
        System.out.println("СПИСОК ПРИОРИТЕТНЫХ ЗАДАЧ:");
        prioritizedTasks.forEach(System.out::println);
    }

    @Override
    public void checkIntersections(Task task) {
        prioritizedTasks.stream()
                .filter(existTask -> task != existTask && task.getStartTime() != null && existTask.getStartTime() != null
                        && task.getEndTime() != null && existTask.getEndTime() != null)
                .filter(existTask -> task.getStartTime().isBefore(existTask.getEndTime()) && task.getEndTime().isAfter(existTask.getStartTime()))
                .findFirst()
                .ifPresent(existTask -> {
                    throw new IntersectionException("Пересечение в задачах");
                });
    }

    protected void calculateEpicEndTime(int id){
        Epic epic = epics.get(id);

        if(subtasks.get(0) != null) {
            Instant  startTime = subtasks.get(0).getStartTime();
            Instant endTime = subtasks.get(0).getEndTime();

            for (Integer subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);

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
        }
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);


        boolean allDone = true;
        boolean inProgress = false;

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(null);
            epic.setDuration(0);
            epic.setEndTime(null);
            return;
        }

        if(!(epic.getSubtaskIds().isEmpty())) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if(subtask != null) {
                    if (subtask.getStatus() != TaskStatus.DONE) {
                        allDone = false;
                    } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                        inProgress = true;
                    }
                }
            }
        }

        if (allDone && epic.getStatus() != TaskStatus.DONE ) {
            epic.setStatus(TaskStatus.DONE);
            epics.put(epicId, epic);
        } else if (inProgress && epic.getStatus() != TaskStatus.IN_PROGRESS) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
            epics.put(epicId, epic);
        } else if (!inProgress && !allDone && epic.getStatus() != TaskStatus.NEW ||epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(Instant.EPOCH);
            epic.setDuration(0);
        }
    }
}
