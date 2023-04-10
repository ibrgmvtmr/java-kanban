package Tasks;

import java.util.*;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public void addSubtask(int id) {
        if (id > 0) {
            subtaskIds.add(id);
        }
    }
    public void cleanSubtaskIds() {
        this.subtaskIds.clear();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }
    public void removeSubtask(int subtaskId) {
        for (int i = 0; i < subtaskIds.size(); i++) {
            if (subtaskIds.get(i) == subtaskId) {
                subtaskIds.remove(i);
                return;
            }
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return id == epic.id &&
                Objects.equals(status, epic.status) &&
                Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
