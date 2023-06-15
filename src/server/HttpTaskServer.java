package server;

import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.taskmanagers.TaskManager;
import server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer{

    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer() throws IOException, InterruptedException {
        TaskManager taskManager = Managers.getInMemoryTaskManger();
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task/", new TaskHandle(taskManager));
        httpServer.createContext("/tasks/epic/", new EpicHandle(taskManager));
        httpServer.createContext("/tasks/subtask/", new SubtaskHandle(taskManager));
        httpServer.createContext("/tasks/subtask/epic/", new SubtasksForEpicHandle(taskManager));
        httpServer.createContext("/tasks/history/", new HistoryHandle(taskManager));
        httpServer.createContext("/tasks/", new PriorityHandle(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
