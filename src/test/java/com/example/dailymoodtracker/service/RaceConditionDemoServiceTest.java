package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.RaceDemoResultDto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RaceConditionDemoServiceTest {

    private final RaceConditionDemoService service = new RaceConditionDemoService();

    @Test
    void runSafeDemo_shouldNotLoseUpdates() {
        RaceDemoResultDto result = service.runSafeDemo(50, 2_000);

        assertEquals("safe", result.mode());
        assertEquals(result.expected(), result.actual());
        assertEquals(0, result.lostUpdates());
    }

    @Test
    void runUnsafeDemo_shouldNotExceedExpected() {
        RaceDemoResultDto result = service.runUnsafeDemo(50, 5_000);

        assertEquals("unsafe", result.mode());
        assertTrue(result.actual() <= result.expected());
        assertTrue(result.lostUpdates() >= 0);
    }
}
