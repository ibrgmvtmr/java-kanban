package Managers;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager<T> implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;

    public List<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyList.size() >= MAX_HISTORY_SIZE) {
            historyList.remove(0);
        }
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyList);
    }
}