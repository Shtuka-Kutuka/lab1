package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.mapper.MoodEntryMapper;
import com.example.dailymoodtracker.model.MoodEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncBusinessWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncBusinessWorker.class);
    private static final String OPERATION_MESSAGE = "get all mood entry";

    private final MoodEntryService moodEntryService;
    private final MoodEntryMapper moodEntryMapper;
    private final AsyncTaskRegistryService taskRegistryService;

    public AsyncBusinessWorker(
        MoodEntryService moodEntryService,
        MoodEntryMapper moodEntryMapper,
        AsyncTaskRegistryService taskRegistryService
    ) {
        this.moodEntryService = moodEntryService;
        this.moodEntryMapper = moodEntryMapper;
        this.taskRegistryService = taskRegistryService;
    }

    @Async("businessTaskExecutor")
    public CompletableFuture<Void> executeTask(UUID taskId, int workUnits) {
        try {

            List<MoodEntry> entries = moodEntryService.findAll();
            long fetched = entries.size();

            taskRegistryService.updateProgress(
                taskId,
                100,
                OPERATION_MESSAGE + ": fetched " + fetched + " mood entries"
            );

            List<MoodEntryDto> dtos = entries.stream()
                .map(moodEntryMapper::toDto)
                .toList();

            taskRegistryService.markSuccess(
                taskId,
                OPERATION_MESSAGE + ": completed",
                dtos
            );
            LOGGER.info("Async task {} completed", taskId);
        } catch (Exception ex) {
            taskRegistryService.markFailed(taskId, OPERATION_MESSAGE + ": failed: " + ex.getMessage());
            LOGGER.error("Async task {} failed", taskId, ex);
        }

        return CompletableFuture.completedFuture(null);
    }
}
