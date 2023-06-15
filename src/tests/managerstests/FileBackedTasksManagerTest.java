package tests.managerstests;

import managers.Managers;
import managers.taskmanagers.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final List<Task> emptyList = new ArrayList<>();
    private Path filePath = Path.of("src/results.csv");

    @BeforeEach
    public void loadInitialConditions() {

        manager = Managers.getDefaultFileBackedTasksManager(filePath);

    }

    @Test
    public void loadFromFileTest() {
        Task newTask = manager.createTask(newTask());
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));

        manager.getEpicById(newEpic.getId());
        manager.getTaskById(newTask.getId());
        manager.getSubtaskById(newSubtask.getId());
        manager = FileBackedTasksManager.loadFromFile(filePath);

        manager.getEpics();
        assertEquals(List.of(newTask), manager.getTasks());
        assertEquals(List.of(newSubtask), manager.getSubtasks());
        assertEquals(List.of(newEpic), manager.getEpics());
        assertEquals(List.of(newTask, newSubtask, newEpic), manager.getHistory());
    }

    @Test
    public void loadFromEmptyFileTest() {
        assertEquals(emptyList, manager.getSubtasks());
        assertEquals(emptyList, manager.getTasks());
        assertEquals(emptyList, manager.getEpics());
    }
    @Test
    public void loadFromEmptyHistoryFileTest() {
        Task newTask = manager.createTask(newTask());
        Epic newEpic = manager.createEpic(newEpic());
        Subtask newSubtask = manager.createSubtask(newSubtask(newEpic));


        assertEquals(List.of(newTask), manager.getTasks());
        assertEquals(List.of(newEpic), manager.getEpics());
        assertEquals(List.of(newSubtask), manager.getSubtasks());
        assertEquals(emptyList, manager.getHistory());
    }

    @Test
    public void loadFromFileWithoutSubtasksTest(){
        Task newTask = manager.createTask(newTask());
        Epic newEpic = manager.createEpic(newEpic());

        assertEquals(List.of(newTask), manager.getTasks());
        assertEquals(List.of(newEpic), manager.getEpics());
    }

    @Test
    public void throwManagerSaveExceptionTest() {
        filePath = Path.of("testfile .exe");
        assertThrows(RuntimeException.class, () -> FileBackedTasksManager.loadFromFile(filePath));
    }

}