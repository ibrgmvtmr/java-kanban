package tests.servertest;

import managers.Managers;
import managers.taskmanagers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.enums.TaskType;
import tests.managerstests.InMemoryTasksManagerTest;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends InMemoryTasksManagerTest {
    private KVServer server;
    private TaskManager manager;

    @BeforeEach
    public void createManager() {
        try {
            server = new KVServer();
            server.start();
            manager = Managers.getDefault("http://localhost:8078");
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void loadTasksTest() {
        Task task1 = new Task("Task1", "Description of Task1", Instant.EPOCH, 0);
        Task task2 = new Task("Task2", "Description of Task2", Instant.EPOCH, 0);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getTasks(), list);
    }

    @Test
    public void loadEpicsTest() {
        Epic epic1 = new Epic("Epic1", "Description of Epic1", TaskType.EPIC);
        Epic epic2 = new Epic("Epic2", "Description of Epic2", TaskType.EPIC);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        assertEquals(epic1, manager.getEpicById(epic1.getId()));
        assertEquals(epic2, manager.getEpicById(epic2.getId()));
        List<Task> list = manager.getHistory();
        assertEquals(manager.getEpics(), list);
    }

    @Test
    public void loadSubtasksTest() {
        Epic epic1 = new Epic("Epic1", "Description of Epic1", TaskType.EPIC);
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Description of Subtask1", Instant.EPOCH, 0, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Description of Subtask2", Instant.EPOCH, 0, epic1.getId());
        Subtask subtask3 = new Subtask("Subtask3", "Description of Subtask1", Instant.EPOCH, 0, epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.getSubtaskById(subtask1.getId());
        manager.getTaskById(subtask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getSubtasks(), list);
    }

}