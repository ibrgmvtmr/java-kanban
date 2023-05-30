package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.time.Instant;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description,Instant startTime, long duration, int epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(int id, String name, TaskStatus taskStatus, String description, Instant startTime, long duration, int epicId) {
        super(id, name, taskStatus, description, startTime, duration);
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
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return id + ","
                + taskType + ","
                + name + ","
                + taskStatus + ","
                + description + ","
                + getStartTime() + ","
                + duration + ","
                + getEndTime() + ","
                + epicId;
    }
}