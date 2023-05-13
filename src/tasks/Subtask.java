package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId) {
        super(name, description, taskStatus);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(int id, String name, TaskStatus taskStatus, String description, int epicId) {
        super(id, name, taskStatus, description);
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
        return id == subtask.id &&
                Objects.equals(taskStatus, subtask.taskStatus) &&
                Objects.equals(name, subtask.name) &&
                Objects.equals(description, subtask.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicId, name, description, taskStatus);
    }

    @Override
    public String toString() {
        return id + ","
                + taskType + ","
                + name + ","
                + taskStatus + ","
                + description + ","
                + epicId;
    }
}