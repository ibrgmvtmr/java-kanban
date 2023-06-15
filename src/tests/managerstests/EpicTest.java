package tests.managerstests;

import managers.taskmanagers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private final List<Task> emptyList = new ArrayList<>();

    protected InMemoryTaskManager manager = new InMemoryTaskManager();

    protected Task newTask() {
        return new Task("Task1", "Description of Task1", Instant.EPOCH, 0);
    }

    protected Epic newEpic() {
        return new Epic("Epic1", "Description of Epic1", TaskType.EPIC);
    }

    protected Subtask newSubtask(Epic epic) {
        return new Subtask("Subtask1", "Description of Subtask1", Instant.EPOCH, 0, epic.getId());
    }

    @Test
    public void hashCodeTaskTest() {
        Task task1 = manager.createTask(newTask());
        assertEquals(List.of(task1).hashCode(), manager.getTasks().hashCode());
    }

    @Test
    public void hashCodeEpicTest() {
        Epic epic1 = manager.createEpic(newEpic());
        assertEquals(List.of(epic1).hashCode(), manager.getEpics().hashCode());
    }

    @Test
    public void hashCodeSubtaskTest() {
        Epic epic1 = manager.createEpic(newEpic());
        Subtask subtask = manager.createSubtask(newSubtask(epic1));

        assertEquals(List.of(subtask).hashCode(), manager.getSubtasks().hashCode());
    }

    @Test
    public void getEpicIdTest() {
        Epic epic1 = manager.createEpic(newEpic());
        Subtask subtask = manager.createSubtask(newSubtask(epic1));

        assertEquals(epic1.getId(), subtask.getEpicId());
    }

    @Test
    public void setEpicEndTimeTest() {
        Epic epic1 = manager.createEpic(newEpic());

        epic1.setEndTime(Instant.ofEpochSecond(42));
        assertEquals(Instant.ofEpochSecond(42), epic1.getEndTime());
    }

    @Test
    public void getTaskTypeTest() {
        Task task1 = manager.createTask(newTask());

        assertEquals(TaskType.TASK, task1.getTaskType());
    }

    @Test
    public void getDuratinTest() {
        Task task1 = manager.createTask(newTask());

        assertEquals(0, task1.getDuration());
    }

    @Test
    public void setDurationTest() {
        Task task1 = manager.createTask(newTask());
        task1.setDuration(56);

        assertEquals(56, task1.getDuration());
    }

    @Test
    public void setStartTimeTest() {
        Task task1 = manager.createTask(newTask());
        task1.setStartTime(Instant.ofEpochSecond(42));

        assertEquals(Instant.ofEpochSecond(42), task1.getStartTime());
    }

    @Test
    public void getTaskNameTest() {
        Task task1 = manager.createTask(newTask());
        assertEquals("Task1", task1.getName());
    }


    @Test
    public void getTaskDescriptionTest() {
        Task task1 = manager.createTask(newTask());

        assertEquals("Description of Task1", task1.getDescription());
    }


    @Test
    public void addSubtaskTest(){
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        newEpic.addSubtask(newSubtask.getId());
        List<Integer> idTest = newEpic.getSubtaskIds();
        assertEquals(idTest, newEpic.getSubtaskIds());
    }

    @Test
    public void cleanSubtaskIdsTest(){
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        newEpic.cleanSubtaskIds();
        assertEquals(emptyList, newEpic.getSubtaskIds());
    }
}