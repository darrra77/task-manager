package com.example.taskmanager.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class TaskStatsService {

    private final UUID uuid = UUID.randomUUID();

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "TaskStatsService{" +
                "uuid=" + uuid +
                '}';
    }
}
