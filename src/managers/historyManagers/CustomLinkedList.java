package managers.historyManagers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class CustomLinkedList {

    private Node<Task> head;
    private Node<Task> tail;

    public Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(tail, task, null);

        if(tail == null) {
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
            current =  current.next;
        }
        return tasks;
    }

    public void removeNode(Node<Task> node) {
        if(node == null) {
            return;
        }

        if (node.equals(head)) {
            head = node.next;

            if(node.next != null) {
                node.next.prev = null;
            }
        } else {
            node.prev.next = node.next;
            if (node.next != null) {
                node.next.prev = node.prev;
            }
        }
    }
}
