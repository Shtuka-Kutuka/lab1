package com.example.dailymoodtracker.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AsyncBusinessService {

    private final AsyncTaskRegistryService taskRegistryService;
    private final AsyncBusinessWorker worker;

    public AsyncBusinessService(AsyncTaskRegistryService taskRegistryService, AsyncBusinessWorker worker) {
        this.taskRegistryService = taskRegistryService;
        this.worker = worker;
    }

    public UUID startTask(int workUnits) {
        UUID taskId = taskRegistryService.createTask();
        // Маркируем задачу как RUNNING синхронно, чтобы POST возвращал taskId уже
        // для реально выполняемого бизнес-метода.
        taskRegistryService.markRunning(taskId, "Executing request: get all mood entry");
        worker.executeTask(taskId, workUnits);
        return taskId;
    }
}
