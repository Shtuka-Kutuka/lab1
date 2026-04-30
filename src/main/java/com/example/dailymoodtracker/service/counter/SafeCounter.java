package com.example.dailymoodtracker.service.counter;

public class SafeCounter implements Counter {
    private long value;

    @Override
    public synchronized void increment() {
        value++;
    }

    @Override
    public synchronized long get() {
        return value;
    }
}