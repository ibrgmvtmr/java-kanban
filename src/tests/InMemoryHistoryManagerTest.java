package tests;

import managers.Managers;
import managers.historymanagers.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskStatus;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    public HistoryManager manager;

    @BeforeEach
    public void loadInitialConditions() {
        this.manager = Managers.getDefaultHistory();
    }

    @Test
    public void addTasksToHistoryTest() {

        Task newTask = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        Epic newEpic = new Epic(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        Subtask newSubtask = new Subtask(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0, newEpic.getId());

        manager.add(newTask);
        manager.add(newEpic);
        manager.add(newSubtask);

        assertEquals(List.of(newTask, newEpic, newSubtask), manager.getHistory(), "История не пустая.");

    }

    @Test
    public void removeFirstElementFromHistory() {
        Task newTask = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        Epic newEpic = new Epic(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        Subtask newSubtask = new Subtask(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0, newEpic.getId());

        manager.add(newTask);
        manager.add(newEpic);
        manager.add(newSubtask);

        manager.remove(newTask.getId());

        assertEquals(List.of(newEpic, newSubtask), manager.getHistory());

    }

    @Test
    public void removeLastlementFromHistory() {
        Task newTask = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        Epic newEpic = new Epic(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        Subtask newSubtask = new Subtask(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0, newEpic.getId());

        manager.add(newTask);
        manager.add(newEpic);
        manager.add(newSubtask);

        manager.remove(newSubtask.getId());

        assertEquals(List.of(newTask, newEpic), manager.getHistory());

    }

    @Test
    public void removeMiddleElementFromHistory() {
        Task newTask = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        Epic newEpic = new Epic(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        Subtask newSubtask = new Subtask(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0, newEpic.getId());

        manager.add(newTask);
        manager.add(newEpic);
        manager.add(newSubtask);

        manager.remove(newEpic.getId());

        assertEquals(List.of(newTask, newSubtask), manager.getHistory());

    }

    @Test
    public void noDuplicatesTest() {

        Task task1 = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        Task task2 = new Task(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        Task task3 = new Task(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task2);
        manager.add(task3);

        assertEquals(List.of(task1, task2, task3), manager.getHistory());

    }

    @Test
    public void noTaskRemoveIfIncorrectIDTest() {
        Task newTask = new Task(1, "Task1", TaskStatus.NEW,
                "Task1", Instant.EPOCH, 0);
        Epic newEpic = new Epic(2, "Task2", TaskStatus.NEW,
                "Task2", Instant.EPOCH, 0);
        Subtask newSubtask = new Subtask(3, "Task3", TaskStatus.NEW,
                "Task3", Instant.EPOCH, 0, newEpic.getId());

        manager.add(newTask);
        manager.add(newEpic);
        manager.add(newSubtask);

        manager.remove(34);
        assertEquals(List.of(newTask, newEpic, newSubtask), manager.getHistory());
    }
}