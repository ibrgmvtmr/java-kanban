public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        // Создаем новую задачу
        Task task = new Task();
        task.setName(" Новая задача");
        task.setDescription("Description of Task 1");
        int taskId = manager.createTask(task);

        // Получаем задачу по ID
        Task retrievedTask = manager.getTaskById(taskId);
        System.out.println(retrievedTask.getName()); // должно напечататься "Новая задача"

        // Обновляем задачу
        retrievedTask.setName("Измененная задача");
        manager.updateTask(retrievedTask);
        Task updatedTask = manager.getTaskById(taskId);
        System.out.println(updatedTask.getName()); // должно напечататься "Измененная задача"

        // Удаляем задачу
       /* manager.deleteTask(taskId);
        Task deletedTask = manager.getTaskById(taskId);
        System.out.println(deletedTask); // должно напечататься "null"*/

        // Создаем новую эпическую задачу
        Epic epic1 = new Epic();
        epic1.setName("Новая эпическая задача");
        epic1.setDescription("Description of Epic 1");
        int epicId = manager.createEpic(epic1);

        // Создаем подзадачу, связанную с эпической задачей
        Subtask subtask1 = new Subtask("Новая подзадача", "Description of Subtask 1", "NEW", epicId);
        int subtaskId = manager.createSubtask(subtask1);

        // Получаем подзадачу по ID
        Subtask retrievedSubtask = manager.getSubtaskById(subtaskId);
        System.out.println(retrievedSubtask.getName()); // должно напечататься "Новая подзадача"

        // Обновляем подзадачу
        retrievedSubtask.setName("Измененная подзадача");
        manager.updateSubtask(retrievedSubtask);
        retrievedSubtask.setStatus("NEW");
        Subtask updatedSubtask = manager.getSubtaskById(subtaskId);
        System.out.println(updatedSubtask.getName()); // должно напечататься "Измененная подзадача"

        /*//Удаляем подзадачу
        manager.deleteSubtaskById(subtaskId);
        Subtask deletedSubtask = manager.getSubtaskById(subtaskId);
        System.out.println(deletedSubtask); // должно напечататься "null"*/

        // Удаляем все задачи и эпические задачи
        /*manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        manager.deleteAllTasks();*/
    }
}
