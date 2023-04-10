package Tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask subtask = (Subtask) obj;
        return epicId == subtask.epicId &&
                Objects.equals(status, subtask.status) &&
                Objects.equals(name, subtask.name) &&
                Objects.equals(description, subtask.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicId, name, description, status);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}