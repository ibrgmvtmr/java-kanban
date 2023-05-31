package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.time.Instant;
import java.util.*;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private Instant endTime = Instant.ofEpochSecond(0);

    public Epic(String name, String description, TaskType taskType) {
        super(name, description, Instant.ofEpochSecond(0), 0);
        this.taskType = taskType;

    }

    public Epic(int id, String name, TaskStatus taskStatus, String description, Instant startTime, long duration){
        super(id, name, taskStatus, description, startTime, duration);
        this.endTime = super.getEndTime();

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
    public Instant getEndTime() {
        Instant endTime = super.getEndTime();
        if (endTime.isBefore(this.endTime)) {
            endTime = this.endTime;
        }
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return id == epic.id &&
                duration == epic.duration &&
                Objects.equals(taskStatus, epic.taskStatus) &&
                Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description) &&
                Objects.equals(startTime, epic.startTime);
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
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
                + getEndTime();
    }
}
