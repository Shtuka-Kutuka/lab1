package com.example.dailymoodtracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncBusinessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncBusinessService.class);

    private final AsyncTaskRegistryService taskRegistryService;

    public AsyncBusinessService(AsyncTaskRegistryService taskRegistryService) {
        this.taskRegistryService = taskRegistryService;
    }

    public UUID startTask(int workUnits) {
        UUID taskId = taskRegistryService.createTask();
        executeTask(taskId, workUnits);
        return taskId;
    }

    @Async("businessTaskExecutor")
    public CompletableFuture<Void> executeTask(UUID taskId, int workUnits) {
        taskRegistryService.markRunning(taskId, "Task is running");

        try {
            for (int step = 1; step <= workUnits; step++) {
                Thread.sleep(80);
                int progress = (int) ((step * 100.0) / workUnits);
                taskRegistryService.updateProgress(taskId, progress, "Processed step " + step + "/" + workUnits);
            }

            taskRegistryService.markSuccess(taskId, "Task completed successfully");
            LOGGER.info("Async task {} completed", taskId);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            taskRegistryService.markFailed(taskId, "Task interrupted");
            LOGGER.warn("Async task {} interrupted", taskId, ex);
        } catch (Exception ex) {
            taskRegistryService.markFailed(taskId, "Task failed: " + ex.getMessage());
            LOGGER.error("Async task {} failed", taskId, ex);
        }

        return CompletableFuture.completedFuture(null);
    }
}
