package com.example.taskmanager.runner;

import com.example.taskmanager.config.AppProperties;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.TaskStatsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

//@Component
public class AppRunner implements CommandLineRunner {

    private final TaskService taskService;
    private final AppProperties appProperties;
    private final ApplicationContext applicationContext;

    public AppRunner(
            TaskService taskService,
            AppProperties appProperties,
            ApplicationContext applicationContext
    ) {
        this.taskService = taskService;
        this.appProperties = appProperties;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {

        System.out.println();
        System.out.println(" Шаг 1. Приветствие ");

        System.out.println(
                "Добро пожаловать в " + appProperties.getName()
                        + "! Лимит: " + appProperties.getMaxTasks()
                        + ". Приоритет по умолчанию: "
                        + appProperties.getDefaultPriority()
        );

        System.out.println();
        System.out.println(" Шаг 2. Добавление задач ");

        /*
        taskService.addTask(
                "Первая задача",
                "Задача с низким приоритетом",
                Priority.LOW
        );

        taskService.addTask(
                "Вторая задача",
                "Задача со средним приоритетом",
                Priority.MEDIUM
        );

        taskService.addTask(
                "Третья задача",
                "Задача с высоким приоритетом",
                Priority.HIGH
        );
        */

        taskService.getAllTasks()
                .forEach(System.out::println);

        System.out.println();
        System.out.println(" Шаг 3. Граничные случаи ");

        /*
        try {
            taskService.addTask(
                    "",
                    "Задача без названия",
                    Priority.LOW
            );
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        try {
            taskService.addTask(
                    "Лишняя задача",
                    "Превышение лимита",
                    Priority.HIGH
            );
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        */

        System.out.println();
        System.out.println(" Шаг 4. Изменение статусов ");

        taskService.updateStatus(1L, Status.IN_PROGRESS);
        taskService.updateStatus(2L, Status.DONE);

        System.out.println("DONE-задачи:");
        taskService.getTasksByStatus(Status.DONE)
                .forEach(System.out::println);

        System.out.println("HIGH-задачи:");
        taskService.getTasksByPriority(Priority.HIGH)
                .forEach(System.out::println);

        System.out.println();
        System.out.println(" Шаг 5. Prototype и ObjectProvider ");

        TaskStatsService stats1 = taskService.getStats();
        TaskStatsService stats2 = taskService.getStats();

        System.out.println("Первый UUID: " + stats1.getUuid());
        System.out.println("Второй UUID: " + stats2.getUuid());

        System.out.println();
        System.out.println(" Шаг 6. ApplicationContext ");

        TaskRepository repository =
                applicationContext.getBean(TaskRepository.class);

        System.out.println(
                "Класс TaskRepository: "
                        + repository.getClass().getSimpleName()
        );

        String[] beanNames =
                applicationContext.getBeanDefinitionNames();

        System.out.println(
                "Общее количество бинов: "
                        + beanNames.length
        );

        System.out.println("Бины, содержащие слово task:");

        Arrays.stream(beanNames)
                .filter(name ->
                        name.toLowerCase().contains("task"))
                .forEach(System.out::println);
    }
}