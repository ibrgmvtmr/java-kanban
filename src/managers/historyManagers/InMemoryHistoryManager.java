package managers.historyManagers;

import tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList tasks;
    private final Map<Integer, Node<Task>> history;

    public InMemoryHistoryManager() {
        tasks = new CustomLinkedList();
        history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        Node<Task> node = tasks.linkLast(task);

        if(history.containsKey(task.getId())) {
            tasks.removeNode(history.get(task.getId()));
            //history.remove(task.getId());
        }

        history.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        tasks.removeNode(history.get(id));
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return tasks.getTasks();
    }
}