package com.example.dailymoodtracker.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CounterServiceTest {

    private final CounterService service = new CounterService();

    @Test
    void incrementAndGet_shouldIncreaseCounter() {
        assertEquals(1, service.incrementAndGet());
        assertEquals(2, service.incrementAndGet());
        assertEquals(2, service.get());
    }

    @Test
    void reset_shouldSetCounterToZero() {
        service.incrementAndGet();
        service.incrementAndGet();

        assertEquals(0, service.reset());
        assertEquals(0, service.get());
    }
}
