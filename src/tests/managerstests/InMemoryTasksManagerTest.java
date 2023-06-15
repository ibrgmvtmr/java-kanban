package tests.managerstests;

import managers.taskmanagers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void loadInitialConditions() {
        manager = new InMemoryTaskManager();
    }
}