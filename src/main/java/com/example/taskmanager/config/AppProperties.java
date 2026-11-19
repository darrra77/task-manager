package com.example.taskmanager.config;

import com.example.taskmanager.model.Priority;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${app.name}")
    private String name;

    @Value("${app.max-tasks}")
    private int maxTasks;

    @Value("${app.default-priority}")
    private Priority defaultPriority;

    @PostConstruct
    public void init() {
        System.out.println(
                "Настройки загружены: "
                        + "name=" + name
                        + ", maxTasks=" + maxTasks
                        + ", defaultPriority=" + defaultPriority
        );
    }

    public String getName() {
        return name;
    }

    public int getMaxTasks() {
        return maxTasks;
    }

    public Priority getDefaultPriority() {
        return defaultPriority;
    }
}
