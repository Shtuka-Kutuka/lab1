package com.example.dailymoodtracker.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class CounterService {

    private final AtomicLong counter = new AtomicLong(0);

    public long incrementAndGet() {
        return counter.incrementAndGet();
    }

    public long get() {
        return counter.get();
    }

    public long reset() {
        counter.set(0);
        return 0;
    }
}
