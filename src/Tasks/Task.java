package Tasks;

import java.util.*;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;

    public Task(String name, String description, Status status){
        this.name = name;
        this.description = description;
        this.status = status;
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

    public Status getStatus() {

        return status;
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

    public void setStatus(Status status) {

        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id &&
                Objects.equals(status, task.status) &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}

