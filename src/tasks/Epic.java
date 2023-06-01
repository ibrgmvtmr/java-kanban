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
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtaskIds.equals(epic.subtaskIds) && endTime.equals(epic.endTime);
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
