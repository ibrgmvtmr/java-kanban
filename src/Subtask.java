class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, String status, int epicId) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}