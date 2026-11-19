package com.example.taskmanager.controller;

import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.taskmanager.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/search")
    public List<Task> search(
            @RequestParam String keyword
    ) {
        return taskService.search(keyword);
    }



    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return taskService.getStatsByStatus();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {

        String statusValue = body.get("status");

        Status status = Status.valueOf(statusValue);

        return taskService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(
            @PathVariable Long id,
            @RequestBody Task task
    ) {
        return taskService.update(id, task)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Task> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority
    ) {
        return taskService.getAll(status, priority);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                taskService.getOrThrow(id)
        );
    }

    @GetMapping("/my")
    public List<Task> getMyTasks(
            @AuthenticationPrincipal User user
    ) {
        return taskService.getMyTasks(user);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Task> create(
            @RequestBody Task task,
            @AuthenticationPrincipal User user
    ) {

        Task createdTask = taskService.addTask(
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                user
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }

}