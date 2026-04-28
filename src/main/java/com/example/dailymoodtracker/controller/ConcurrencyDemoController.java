package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.CounterValueDto;
import com.example.dailymoodtracker.dto.RaceDemoResultDto;
import com.example.dailymoodtracker.service.CounterService;
import com.example.dailymoodtracker.service.RaceConditionDemoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/concurrency")
@Tag(name = "Concurrency Demo", description = "Thread-safety and race condition API")
public class ConcurrencyDemoController {

    private final CounterService counterService;
    private final RaceConditionDemoService raceConditionDemoService;

    public ConcurrencyDemoController(
        CounterService counterService,
        RaceConditionDemoService raceConditionDemoService
    ) {
        this.counterService = counterService;
        this.raceConditionDemoService = raceConditionDemoService;
    }

    @Operation(summary = "Increment thread-safe counter")
    @PostMapping("/counter/increment")
    public CounterValueDto incrementCounter() {
        return new CounterValueDto(counterService.incrementAndGet());
    }

    @Operation(summary = "Read thread-safe counter")
    @GetMapping("/counter/value")
    public CounterValueDto getCounterValue() {
        return new CounterValueDto(counterService.get());
    }

    @Operation(summary = "Reset thread-safe counter")
    @PostMapping("/counter/reset")
    public CounterValueDto resetCounter() {
        return new CounterValueDto(counterService.reset());
    }

    @Operation(summary = "Run race condition demo in selected mode")
    @PostMapping("/race-demo")
    public RaceDemoResultDto raceDemo(
        @RequestParam(defaultValue = "unsafe") String mode,
        @RequestParam(defaultValue = "64") int threads,
        @RequestParam(defaultValue = "10000") int incrementsPerThread
    ) {
        validateDemoParams(threads, incrementsPerThread);
        if ("safe".equalsIgnoreCase(mode)) {
            return raceConditionDemoService.runSafeDemo(threads, incrementsPerThread);
        }
        return raceConditionDemoService.runUnsafeDemo(threads, incrementsPerThread);
    }

    @Operation(summary = "Run both unsafe and safe race demos")
    @PostMapping("/race-demo/compare")
    public List<RaceDemoResultDto> compareRaceDemo(
        @RequestParam(defaultValue = "64") int threads,
        @RequestParam(defaultValue = "10000") int incrementsPerThread
    ) {
        validateDemoParams(threads, incrementsPerThread);
        return List.of(
            raceConditionDemoService.runUnsafeDemo(threads, incrementsPerThread),
            raceConditionDemoService.runSafeDemo(threads, incrementsPerThread)
        );
    }

    private void validateDemoParams(int threads, int incrementsPerThread) {
        if (threads < 50) {
            throw new IllegalArgumentException("threads must be >= 50");
        }
        if (incrementsPerThread < 1) {
            throw new IllegalArgumentException("incrementsPerThread must be >= 1");
        }
    }
}
