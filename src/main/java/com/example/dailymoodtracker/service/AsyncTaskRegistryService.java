package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.async.AsyncTaskStatus;
import com.example.dailymoodtracker.dto.AsyncTaskStatusDto;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AsyncTaskRegistryService {

    private final Map<UUID, TaskState> tasks = new ConcurrentHashMap<>();

    public UUID createTask() {
        UUID taskId = UUID.randomUUID();
        tasks.put(taskId, TaskState.pending(taskId));
        return taskId;
    }

    public AsyncTaskStatusDto getTask(UUID taskId) {
        return stateById(taskId).snapshot();
    }

    public void markRunning(UUID taskId, String message) {
        stateById(taskId).markRunning(message);
    }

    public void updateProgress(UUID taskId, int progressPercent, String message) {
        stateById(taskId).updateProgress(progressPercent, message);
    }

    public void markSuccess(UUID taskId, String message) {
        stateById(taskId).markSuccess(message);
    }

    public void markFailed(UUID taskId, String message) {
        stateById(taskId).markFailed(message);
    }

    private TaskState stateById(UUID taskId) {
        TaskState state = tasks.get(taskId);
        if (state == null) {
            throw new ResourceNotFoundException("Task not found: " + taskId);
        }
        return state;
    }

    private static final class TaskState {

        private final UUID taskId;
        private final Instant createdAt;
        private AsyncTaskStatus status;
        private Instant startedAt;
        private Instant finishedAt;
        private int progressPercent;
        private String message;

        private TaskState(UUID taskId) {
            this.taskId = taskId;
            this.createdAt = Instant.now();
            this.status = AsyncTaskStatus.PENDING;
            this.progressPercent = 0;
            this.message = "Task created";
        }

        static TaskState pending(UUID taskId) {
            return new TaskState(taskId);
        }

        synchronized void markRunning(String value) {
            this.status = AsyncTaskStatus.RUNNING;
            this.startedAt = Instant.now();
            this.message = value;
        }

        synchronized void updateProgress(int value, String text) {
            this.progressPercent = Math.clamp(value, 0, 100);
            this.message = text;
        }

        synchronized void markSuccess(String value) {
            this.status = AsyncTaskStatus.SUCCESS;
            this.progressPercent = 100;
            this.finishedAt = Instant.now();
            this.message = value;
        }

        synchronized void markFailed(String value) {
            this.status = AsyncTaskStatus.FAILED;
            this.finishedAt = Instant.now();
            this.message = value;
        }

        synchronized AsyncTaskStatusDto snapshot() {
            return new AsyncTaskStatusDto(
                taskId,
                status,
                createdAt,
                startedAt,
                finishedAt,
                progressPercent,
                message
            );
        }
    }
}
