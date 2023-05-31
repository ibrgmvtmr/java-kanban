package tests;

import exceptions.IntersectionException;
import managers.historymanagers.HistoryManager;
import managers.historymanagers.InMemoryHistoryManager;
import managers.taskmanagers.FileBackedTasksManager;
import managers.taskmanagers.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest  <T extends TaskManager> {
    private final List<Task> emptyList = new ArrayList<>();

    protected T manager;

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
    public void createTaskTest() {
        Task task1 = manager.createTask(newTask());
        List<Task> tasks = manager.getTasks();

        assertEquals(List.of(task1), tasks);
    }

    @Test
    public void createEpicTest() {
        Epic epic1 = manager.createEpic(newEpic());
        List<Epic> epics = manager.getEpics();

        assertEquals(List.of(epic1), epics);
    }

    @Test
    public void createSubtaskTest() {
        Epic epic1 = manager.createEpic(newEpic());
        Subtask subtask1 = manager.createSubtask(newSubtask(epic1));
        List<Subtask> subtasks = manager.getSubtasks();

        assertEquals(List.of(subtask1), subtasks);
    }

    @Test
    public void updateTaskStatusTest() {
        Task newTask = manager.createTask(newTask());

        newTask.setStatus(TaskStatus.IN_PROGRESS);
        TaskStatus updatedTaskStatus = manager.getTaskById(newTask.getId()).getStatus();

        assertEquals(TaskStatus.IN_PROGRESS, updatedTaskStatus);
    }

    @Test
    public void updateEpicTest() {

        Epic newEpic = manager.createEpic(newEpic());

        newEpic.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateEpicStatus(newEpic.getId());

        TaskStatus updatedEpicState = manager.getEpicById(newEpic.getId()).getStatus();

        assertEquals(TaskStatus.DONE, updatedEpicState);

    }

    @Test
    public void updateEpicStatusNewTest() {

        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        TaskStatus updatedStatus = manager.getEpicById(newEpic.getId()).getStatus();

        assertEquals(TaskStatus.NEW, updatedStatus);

    }

    @Test
    public void updateSubtaskStatusDoneTest() {
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        newSubtask.setStatus(TaskStatus.DONE);
        manager.updateEpicStatus(newSubtask.getEpicId());

        TaskStatus updatedEpicState = manager.getEpicById(newEpic.getId()).getStatus();
        TaskStatus updatedSubtaskState = manager.getSubtaskById(newSubtask.getId()).getStatus();

        assertEquals(TaskStatus.DONE, updatedEpicState);
        assertEquals(TaskStatus.DONE, updatedSubtaskState);

    }


    @Test
    public void deleteTaskTest(){
        Task newTask = manager.createTask(newTask());

        manager.deleteTask(newTask.getId());
        assertEquals(emptyList, manager.getTasks());
    }


    @Test
    public void deleteEpicTest(){
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        manager.deleteEpic(newEpic.getId());
        assertEquals(emptyList, manager.getEpics());
        assertEquals(emptyList, manager.getSubtasks());

    }

    @Test
    public void deleteSubtaskTest(){
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        manager.deleteSubtask(newSubtask.getId());
        assertEquals(emptyList, manager.getSubtasks());
    }

    @Test
    public void noSubtaskDeletedIfIncorrectIDTest() {
        Epic newEpic = manager.createEpic(newEpic());

        manager.deleteSubtask(42);

        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    public void noTaskRemoveIfIncorrectIDTest() {
        Task newTask = manager.createTask(newTask());

        manager.deleteSubtask(42);

        assertEquals(List.of(newTask), manager.getTasks());
    }

    @Test
    public void noEpicRemoveIfIncorrectIDTest() {

        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        manager.deleteEpic(42);

        assertEquals(List.of(newEpic), manager.getEpics());

    }

    @Test
    public void deleteTasksTest(){
        Task newTask = manager.createTask(newTask());

        manager.deleteTasks();
        assertEquals(emptyList, manager.getTasks());
    }

    @Test
    public void deleteEpicsTest() {
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        manager.deleteEpics();
        assertEquals(emptyList, manager.getEpics());
        assertEquals(emptyList, manager.getSubtasks());
    }

    @Test
    public void deleteSubtasksTest(){
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        manager.deleteSubtasks();
        assertEquals(emptyList, manager.getSubtasks());

    }

    @Test
    public void calculateStartAndEndTimeOfEpicTest() {
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask1 = manager.createSubtask(newSubtask(newEpic));
         Subtask newSubtask2 = manager.createSubtask(new Subtask(
                "Task1", "Task1",
                Instant.ofEpochMilli(502233423L), 50, newEpic.getId()));


        assertEquals(newSubtask1.getStartTime(), newEpic.getStartTime());
        assertEquals(newSubtask2.getEndTime(), newEpic.getEndTime());
    }

    @Test
    public void tasksToStringTest() {

        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        assertEquals(newEpic + "\n" + newSubtask + "\n", FileBackedTasksManager.toString(manager));

    }

    @Test
    public void tasksFromStringTest() {

        Task realTask = new Task(1, "Task1", TaskStatus.NEW,
                "Description of Task1", Instant.EPOCH, 30);

        Task taskFromString = FileBackedTasksManager.fromString(
                "1,TASK,Task1,NEW,Description of Task1,1970-01-01T00:00:00Z,30,1970-01-01T00:00:00Z");

        assertEquals(realTask, taskFromString);
    }

    @Test
    public void throwIllegalArgumentExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> FileBackedTasksManager.fromString(
                "1,MASK,Task1,NEW,Task1,1970-01-01T00:00:00Z,30,1970-01-01T00:00:00Z"
        ));
    }

    @Test
    public void returnEmptyHistoryTest() {
        assertEquals(emptyList, manager.getHistory());
    }

    @Test
    public void returnHistoryWithTasksTest() {
        Task newTask = manager.createTask(newTask());
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        manager.getTaskById(newTask.getId());
        manager.getEpicById(newEpic.getId());
        manager.getSubtaskById(newSubtask.getId());

        assertEquals(List.of(newTask, newEpic, newSubtask), manager.getHistory(), "История не пустая.");
    }

    @Test
    public void throwIntersectionExceptionTest() {
        assertThrows(IntersectionException.class, () -> {
            manager.createTask(new Task(
                    "Task1", "Task1",
                    Instant.ofEpochMilli(42), 42));
            manager.createTask(new Task(
                    "Task2", "Task2",
                    Instant.ofEpochMilli(42), 42));
        });
    }

    @Test
    public void getPrioritizedTasksTest(){
        Task newTask = manager.createTask(new Task(
                "Task1", "Task1",
                Instant.ofEpochMilli(42), 42));
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(new Subtask
                ("Subtask1", "Subtask1", Instant.ofEpochMilli(67), 67, newEpic.getId()));

        assertEquals(List.of (newTask, newSubtask), manager.getPrioritizedTasks());
    }
    @Test
    public void historyFromStringTest() {
        assertEquals(List.of(1, 2), FileBackedTasksManager.historyFromString("1,2"));
    }

    @Test
    public void historyToStringTest() {
        HistoryManager historyManager = new InMemoryHistoryManager();

        Task newTask = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        Epic newEpic = new Epic(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        Subtask newSubtask = new Subtask(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0, newEpic.getId());

        historyManager.add(newTask);
        historyManager.add(newEpic);
        historyManager.add(newSubtask);

        assertEquals(newTask.getId() + ","
                        + newEpic.getId() + ","
                        + newSubtask.getId() + ",",
                FileBackedTasksManager.historyToString(historyManager));
    }
}