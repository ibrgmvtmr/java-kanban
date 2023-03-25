import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int generatedId= 1;

    private HashMap<Integer, Task> tasks= new HashMap<>();
    private HashMap<Integer, Subtask>  subtasks= new HashMap<>();
    private HashMap<Integer, Epic> epics= new HashMap<>();


    public int createTask(Task task) {
        task.setId(generatedId++);
        task.setStatus("NEW");
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public int createEpic(Epic epic){
        epic.setId(generatedId);
        epic.setStatus("NEW");
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

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

    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtasksToDelete = epic.getSubtaskIds();
            for (Integer subtask : subtasksToDelete) {
                subtasks.remove(subtask);
            }
        }
        epics.clear();
    }


    public void deleteEpic(int id){
        if(epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Integer> subtasksToDelete = epic.getSubtaskIds();
            for (Integer subtask : subtasksToDelete) {
                subtasks.remove(subtask);
            }
            epics.remove(id);
        }
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public int createSubtask(Subtask subtask) {
        subtask.setId(generatedId++);
        subtask.setStatus("NEW");
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpicById(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
            updateEpicStatus(epic.getId());
        }
        return subtask.getId();
    }

    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            if (subtasks.containsKey(subtask.getId())) {
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(epic.getId());
            }
        }
    }
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
    }

    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            int epicId = subtask.getEpicId();
            if (epicId > 0) {
                Epic epic = epics.get(epicId);
                if (epic != null) {
                    epic.removeSubtask(subtask.getId());
                    updateEpicStatus(epicId); // добавлен вызов метода обновления статуса эпика
                }
            }
        }
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        boolean allDone = true;
        boolean inProgress = false;
        for (Integer subtaskId : epic.getSubtaskIds()){
            Subtask subtask = subtasks.get(subtaskId);
            if(!subtask.getStatus().equals("DONE")) {
                allDone= false;
            }
            if(subtask.getStatus().equals("IN_PROGRESS")) {
                inProgress= true;
            }
        }
        if (allDone && epic.getSubtaskIds().isEmpty()) {
            epic.setStatus("DONE");
            epics.put(epicId, epic);
        } else if (inProgress) {
            epic.setStatus("IN_PROGRESS");
            epics.put(epicId, epic);
        } else {
            epic.setStatus("NEW");
            epics.put(epicId, epic);
        }
    }
}
