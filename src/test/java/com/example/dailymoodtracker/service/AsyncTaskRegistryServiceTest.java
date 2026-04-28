package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.async.AsyncTaskStatus;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AsyncTaskRegistryServiceTest {

    private final AsyncTaskRegistryService service = new AsyncTaskRegistryService();

    @Test
    void createTask_shouldReturnPendingStatus() {
        UUID taskId = service.createTask();

        var state = service.getTask(taskId);
        assertEquals(taskId, state.taskId());
        assertEquals(AsyncTaskStatus.PENDING, state.status());
        assertEquals(0, state.progressPercent());
    }

    @Test
    void lifecycle_shouldUpdateTaskState() {
        UUID taskId = service.createTask();
        service.markRunning(taskId, "running");
        service.updateProgress(taskId, 80, "almost done");
        service.markSuccess(taskId, "done");

        var state = service.getTask(taskId);
        assertEquals(AsyncTaskStatus.SUCCESS, state.status());
        assertEquals(100, state.progressPercent());
        assertEquals("done", state.message());
    }

    @Test
    void getTask_unknownId_shouldThrowNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> service.getTask(UUID.randomUUID()));
    }
}
