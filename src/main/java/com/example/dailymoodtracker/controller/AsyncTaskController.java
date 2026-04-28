package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.AsyncTaskStartRequestDto;
import com.example.dailymoodtracker.dto.AsyncTaskStartResponseDto;
import com.example.dailymoodtracker.dto.AsyncTaskStatusDto;
import com.example.dailymoodtracker.service.AsyncBusinessService;
import com.example.dailymoodtracker.service.AsyncTaskRegistryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/async-tasks")
@Tag(name = "Async Tasks", description = "Asynchronous operations API")
public class AsyncTaskController {

    private final AsyncBusinessService asyncBusinessService;
    private final AsyncTaskRegistryService taskRegistryService;

    public AsyncTaskController(
        AsyncBusinessService asyncBusinessService,
        AsyncTaskRegistryService taskRegistryService
    ) {
        this.asyncBusinessService = asyncBusinessService;
        this.taskRegistryService = taskRegistryService;
    }

    @Operation(summary = "Start async business operation")
    @PostMapping("/start")
    public AsyncTaskStartResponseDto startTask(@Valid @RequestBody AsyncTaskStartRequestDto dto) {
        UUID taskId = asyncBusinessService.startTask(dto.workUnits());
        return new AsyncTaskStartResponseDto(taskId);
    }

    @Operation(summary = "Get async task status")
    @GetMapping("/{taskId}")
    public AsyncTaskStatusDto getStatus(@PathVariable UUID taskId) {
        return taskRegistryService.getTask(taskId);
    }
}
