package com.example.taskmanager.controller;

import com.example.taskmanager.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.taskmanager.model.Task;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/web/tasks")
public class TaskWebController {

    private final TaskService taskService;

    public TaskWebController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getOrThrow(id));
        return "tasks/detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("taskRequest", new TaskRequest());
        model.addAttribute("priorities", Priority.values());
        return "tasks/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("taskRequest") TaskRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            return "tasks/form";
        }

        taskService.addTask(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                user
        );

        return "redirect:/web/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Task task = taskService.getOrThrow(id);

        TaskRequest request = new TaskRequest();
        request.setTitle(task.getTitle());
        request.setDescription(task.getDescription());
        request.setPriority(task.getPriority());

        model.addAttribute("taskRequest", request);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("taskId", id);

        return "tasks/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("taskRequest") TaskRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("taskId", id);
            return "tasks/edit";
        }

        Task task = taskService.getOrThrow(id);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());

        taskService.update(id, task);

        return "redirect:/web/tasks/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/web/tasks";
    }

}
