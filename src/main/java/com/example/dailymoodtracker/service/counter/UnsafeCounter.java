package com.example.dailymoodtracker.service.counter;

public class UnsafeCounter implements Counter {
    private long value;

    @Override
    public void increment() {
        value++;
    }

    @Override
    public long get() {
        return value;
    }
}