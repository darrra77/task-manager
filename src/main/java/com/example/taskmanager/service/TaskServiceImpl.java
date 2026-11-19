package com.example.taskmanager.service;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ObjectProvider<TaskStatsService> taskStatsServiceProvider;

    @Value("${app.max-tasks}")
    private int maxTasks;

    @Value("${app.default-priority}")
    private Priority defaultPriority;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            ObjectProvider<TaskStatsService> taskStatsServiceProvider
    ) {
        this.taskRepository = taskRepository;
        this.taskStatsServiceProvider = taskStatsServiceProvider;
    }

    @PostConstruct
    public void init() {
        System.out.println("TaskService инициализирован");
    }

    @PreDestroy
    public void destroy() {
        System.out.println(
                "Завершение работы. Задач в хранилище: "
                        + taskRepository.findAll().size()
        );
    }

    @Override
    public List<Task> search(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        return taskRepository.searchByKeyword(keyword);
    }

    @Override
    public Map<String, Long> getStatsByStatus() {

        Map<String, Long> stats = new HashMap<>();

        for (Object[] row : taskRepository.countGroupedByStatus()) {
            Status status = (Status) row[0];
            Long count = (Long) row[1];

            stats.put(status.name(), count);
        }

        return stats;
    }

    @Override
    public List<Task> getAll(String status, String priority) {

        if (status != null && priority != null) {
            return taskRepository.findByStatusAndPriority(
                    Status.valueOf(status.toUpperCase()),
                    Priority.valueOf(priority.toUpperCase())
            );
        }

        if (status != null) {
            return taskRepository.findByStatus(
                    Status.valueOf(status.toUpperCase())
            );
        }

        if (priority != null) {
            return taskRepository.findByPriority(
                    Priority.valueOf(priority.toUpperCase())
            );
        }

        return taskRepository.findAll();
    }

    @Override
    public Task getOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public Optional<Task> update(Long id, Task updatedTask) {

        Optional<Task> existingTask = taskRepository.findById(id);

        if (existingTask.isEmpty()) {
            return Optional.empty();
        }

        if (updatedTask.getTitle() == null || updatedTask.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Название задачи не может быть пустым"
            );
        }

        Task task = existingTask.get();

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());

        if (updatedTask.getPriority() != null) {
            task.setPriority(updatedTask.getPriority());
        }

        return Optional.of(taskRepository.save(task));
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public Task addTask(
            String title,
            String description,
            Priority priority,
            User owner
    ) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Название задачи не может быть пустым"
            );
        }

        if (taskRepository.count() >= maxTasks) {
            throw new IllegalStateException(
                    "Достигнут максимальный лимит задач: " + maxTasks
            );
        }

        Priority finalPriority = priority != null
                ? priority
                : defaultPriority;

        Task task = new Task(
                null,
                title,
                description,
                finalPriority,
                Status.NEW
        );

        task.setOwner(owner);

        return taskRepository.save(task);
    }

    @Override
    public List<Task> getMyTasks(User owner) {
        return taskRepository.findAllByOwner(owner);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getTasksByStatus(Status status) {
        return taskRepository.findAll()
                .stream()
                .filter(task -> task.getStatus() == status)
                .toList();
    }

    @Override
    public List<Task> getTasksByPriority(Priority priority) {
        return taskRepository.findAll()
                .stream()
                .filter(task -> task.getPriority() == priority)
                .toList();
    }

    @Override
    public Optional<Task> updateStatus(Long id, Status status) {

        Optional<Task> existingTask = taskRepository.findById(id);

        if (existingTask.isEmpty()) {
            return Optional.empty();
        }

        Task task = existingTask.get();

        task.setStatus(status);

        return Optional.of(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskStatsService getStats() {
        return taskStatsServiceProvider.getObject();
    }
}