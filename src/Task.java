import java.util.*;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected String status;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
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

    public void setStatus(String status) {

        this.status = status;
    }
}
