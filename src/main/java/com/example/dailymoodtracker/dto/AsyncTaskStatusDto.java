package com.example.dailymoodtracker.dto;

import com.example.dailymoodtracker.async.AsyncTaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AsyncTaskStatusDto(
    UUID taskId,
    AsyncTaskStatus status,
    Instant createdAt,
    Instant startedAt,
    Instant finishedAt,
    int progressPercent,
    String message,
    List<MoodEntryDto> result
) { }
