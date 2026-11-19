package com.example.taskmanager.service;

import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;

import java.util.Map;
import java.util.Optional;
import java.util.List;

public interface TaskService {

    Task addTask(String title, String description, Priority priority, User owner);

    Task getOrThrow(Long id);

    Optional<Task> update(Long id, Task task);

    Optional<Task> findById(Long id);

    List<Task> getMyTasks(User owner);

    List<Task> search(String keyword);

    List<Task> getAllTasks();

    List<Task> getTasksByStatus(Status status);

    List<Task> getTasksByPriority(Priority priority);

    List<Task> getAll(String status, String priority);

    Optional<Task> updateStatus(Long id, Status status);

    void deleteTask(Long id);

    Map<String, Long> getStatsByStatus();

    TaskStatsService getStats();
}
