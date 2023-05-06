package managers.taskManagers;
import managers.Managers;
import managers.historyManagers.HistoryManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int generatedId= 1;

    protected final HistoryManager historyManager;

    private final HashMap<Integer, Task> tasks= new HashMap<>();
    private final HashMap<Integer, Subtask>  subtasks= new HashMap<>();
    private final HashMap<Integer, Epic> epics= new HashMap<>();

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }


    @Override
    public int createTask(Task task) {
        task.setId(generatedId++);
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
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
    public int createEpic(Epic epic){
        epic.setId(generatedId++);
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic.getId();
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
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                if (subtasks.containsKey(subtaskId)) {
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
    public int createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtask.setId(generatedId++);
            subtask.setStatus(Status.NEW);
            epic.addSubtask(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epic.getId());
            return subtask.getId();
        } else {
            System.out.println("Ошибк: Эпик с таким ID " + subtask.getEpicId() + " не существует ");
            return 0;
        }
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
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtasks.remove(id);
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
        System.out.println(historyManager.getHistory());
        return historyManager.getHistory();
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        boolean allDone = true;
        boolean inProgress = false;
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                inProgress = true;
            }
        }
        if (allDone && epic.getStatus() != Status.DONE) {
            epic.setStatus(Status.DONE);
            epics.put(epicId, epic);
        } else if (inProgress && epic.getStatus() != Status.IN_PROGRESS) {
            epic.setStatus(Status.IN_PROGRESS);
            epics.put(epicId, epic);
        } else if (!inProgress && !allDone && epic.getStatus() != Status.NEW) {
            epic.setStatus(Status.NEW);
            epics.put(epicId, epic);
        }
    }
}
