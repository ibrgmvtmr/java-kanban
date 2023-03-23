import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int generatedId= 1;

    HashMap<Integer, Task> tasks= new HashMap<>();
    HashMap<Integer, Subtask>  subtasks= new HashMap<>();
    HashMap<Integer, Epic> epics= new HashMap<>();


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

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public ArrayList<Task> getAllTasks() {
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
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteAllEpics(){
        epics.clear();
    }

    public void deleteEpic(int id){
        if(epics.containsKey(id)) {
            epics.remove(id);
        }
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public int createSubtask(Subtask subtask) {
        subtask.setId(generatedId++);
        subtask.setStatus("NEW");
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpicById(subtask.getEpicId());
        epic.addSubtask(subtask);
        updateEpic(epic);
        return subtask.getId();
    }


    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = getEpicById(subtask.getEpicId());
            updateEpicStatus(epic.getId());
        }
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtasks.remove(id);
            Epic epic = getEpicById(subtask.getEpicId());
            epic.getSubtaskIds().remove(Integer.valueOf(id));
             if (epic.getStatus().equals("DONE")) {
                epic.setStatus("IN_PROGRESS");
            }
        }
    }

    public void deleteAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            subtasks.remove(subtaskId);
        }
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
        }
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getSubtaskslistByEpic(int epicId) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasksList.add(subtask);
            }
        }
        return subtasksList;
    }


    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        boolean allDone = true;
        boolean inProgress = false;
        for (Integer subtaskId : epic.subtaskIds){
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
