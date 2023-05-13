package tasks;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.util.*;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus taskStatus;
    protected TaskType taskType;

    public Task(int id, String name, TaskStatus taskStatus, String description){
        this.id = id;
        this.taskType = TaskType.TASK;
        this.name = name;
        this.taskStatus = taskStatus;
        this.description = description;
    }

    public Task(String name, String description, TaskStatus taskStatus){
        this.name = name;
        this.taskType = TaskType.TASK;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    public String getName() {

        return name;
    }

    public String getDescription() {

        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {

        return taskStatus;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setStatus(TaskStatus taskStatus) {

        this.taskStatus = taskStatus;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id &&
                Objects.equals(taskStatus, task.taskStatus) &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, taskStatus);
    }

    @Override
    public String toString() {
        return id + ","
                + taskType + ","
                + name + ","
                + taskStatus + ","
                + description;
    }
}

