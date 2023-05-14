package managers.taskmanagers;
import managers.Managers;
import managers.historymanagers.HistoryManager;
import tasks.Epic;
import tasks.enums.TaskStatus;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    protected int generatedId= 1;

    protected HistoryManager historyManager;

    protected final HashMap<Integer, Task> tasks= new HashMap<>();
    protected final HashMap<Integer, Subtask>  subtasks= new HashMap<>();
    protected final HashMap<Integer, Epic> epics= new HashMap<>();

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int createTask(Task task) throws IOException {
        task.setId(generatedId++);
        task.setStatus(TaskStatus.NEW);
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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
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
    public int createEpic(Epic epic) throws IOException {
        epic.setId(generatedId++);
        epic.setStatus(TaskStatus.NEW);
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
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
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
    public int createSubtask(Subtask subtask) throws IOException {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtask.setId(generatedId++);
            subtask.setStatus(TaskStatus.NEW);
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
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
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

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        boolean allDone = true;
        boolean inProgress = false;
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                inProgress = true;
            }
        }
        if (allDone && epic.getStatus() != TaskStatus.DONE) {
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
