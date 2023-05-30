package managers.historymanagers;

import tasks.Task;

import java.util.ArrayList;
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
        if(task != null) {
            Node<Task> node = tasks.linkLast(task);
            if (history.containsKey(task.getId())) {
                tasks.removeNode(history.get(task.getId()));
            }

            history.put(task.getId(), node);
        }
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

    public static class CustomLinkedList {

        private Node<Task> head;
        private Node<Task> tail;

        public Node<Task> linkLast(Task task) {
            Node<Task> newNode = new Node<>(tail, task, null);

            if (tail == null) {
                head = newNode;
            } else {
                tail.next = newNode;
            }
            tail = newNode;

            return newNode;
        }

        public List<Task> getTasks() {

            List<Task> tasks = new ArrayList<>();
            Node<Task> current = head;

            while (current != null) {
                tasks.add(current.item);
                current = current.next;
            }
            return tasks;
        }

        public void removeNode(Node<Task> node) {
            if (node == null) {
                return;
            }

            if (node.equals(head)) {
                head = node.next;

                if (node.next != null) {
                    node.next.prev = null;
                } else {
                    tail = null;
                }
            } else {
                node.prev.next = node.next;
                if (node.next != null) {
                    node.next.prev = node.prev;
                } else {
                    tail = node.prev;
                }
            }
        }
    }
}

