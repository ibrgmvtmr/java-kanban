import java.util.ArrayList;

class Epic extends Task {
    ArrayList<Integer> subtaskIds;

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public Epic() {
        this.subtaskIds = new ArrayList<>();
    }
    public void addSubtask(Subtask subtask) {
        subtaskIds.add(subtask.getId());
    }

    public void removeAllSubtasks() {
        subtaskIds.clear();
    }

}