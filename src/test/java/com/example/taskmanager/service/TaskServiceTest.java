package com.example.taskmanager.service;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ObjectProvider<TaskStatsService> taskStatsServiceProvider;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(taskService, "maxTasks", 10);
        ReflectionTestUtils.setField(
                taskService,
                "defaultPriority",
                Priority.MEDIUM
        );
    }

    @Test
    void getAll_shouldReturnTasks() {

        Task task = buildTask(
                1L,
                "Купить молоко",
                Priority.HIGH,
                Status.NEW
        );

        when(taskRepository.findAll())
                .thenReturn(List.of(task));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle())
                .isEqualTo("Купить молоко");
    }

    @Test
    void getAll_whenEmpty_shouldReturnEmptyList() {

        when(taskRepository.findAll())
                .thenReturn(List.of());

        List<Task> result = taskService.getAllTasks();

        assertThat(result).isEmpty();
    }

    @Test
    void create_shouldSaveTaskWithStatusNew() {

        when(taskRepository.count()).thenReturn(0L);

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> {
                    Task task = invocation.getArgument(0);
                    task.setId(1L);
                    return task;
                });

        User user = new User(
                1L,
                "user@test.com",
                "password",
                Role.ROLE_USER
        );

        Task result = taskService.addTask(
                "Новая задача",
                "Описание",
                Priority.HIGH,
                user
        );

        assertThat(result.getStatus())
                .isEqualTo(Status.NEW);

        assertThat(result.getId())
                .isEqualTo(1L);

        verify(taskRepository)
                .save(any(Task.class));

        assertThat(result.getOwner())
                .isEqualTo(user);
    }

    @Test
    void findById_whenExists_shouldReturnTask() {

        Task task = buildTask(
                1L,
                "Задача",
                Priority.MEDIUM,
                Status.NEW
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        Optional<Task> result =
                taskService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle())
                .isEqualTo("Задача");
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<Task> result =
                taskService.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void update_whenExists_shouldReturnUpdatedTask() {

        Task existing = buildTask(
                1L,
                "Старое название",
                Priority.LOW,
                Status.NEW
        );

        Task updated = buildTask(
                null,
                "Новое название",
                Priority.HIGH,
                Status.NEW
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Optional<Task> result =
                taskService.update(1L, updated);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle())
                .isEqualTo("Новое название");
        assertThat(result.get().getPriority())
                .isEqualTo(Priority.HIGH);
    }

    @Test
    void update_whenNotExists_shouldReturnEmpty() {

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        Task updated = buildTask(
                null,
                "Новое название",
                Priority.HIGH,
                Status.NEW
        );

        Optional<Task> result =
                taskService.update(999L, updated);

        assertThat(result).isEmpty();
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {

        taskService.deleteTask(1L);

        verify(taskRepository)
                .deleteById(1L);
    }

    @Test
    void updateStatus_whenExists_shouldUpdateStatus() {

        Task task = buildTask(
                1L,
                "Задача",
                Priority.HIGH,
                Status.NEW
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Optional<Task> result =
                taskService.updateStatus(
                        1L,
                        Status.DONE
                );

        assertThat(result).isPresent();
        assertThat(result.get().getStatus())
                .isEqualTo(Status.DONE);

        verify(taskRepository)
                .save(task);
    }

    @Test
    void getOrThrow_whenNotExists_shouldThrowTaskNotFoundException() {

        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> taskService.getOrThrow(999L)
        )
                .isInstanceOf(
                        TaskNotFoundException.class
                )
                .hasMessageContaining("999");
    }

    private Task buildTask(
            Long id,
            String title,
            Priority priority,
            Status status
    ) {
        Task task = new Task();

        task.setId(id);
        task.setTitle(title);
        task.setDescription("Описание");
        task.setPriority(priority);
        task.setStatus(status);

        return task;
    }
}