package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.RaceDemoResultDto;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class RaceConditionDemoService {

    public RaceDemoResultDto runUnsafeDemo(int threads, int incrementsPerThread) {
        long expected = (long) threads * incrementsPerThread;
        long actual = runUnsafeCounter(threads, incrementsPerThread);
        return new RaceDemoResultDto(
            "unsafe",
            threads,
            incrementsPerThread,
            expected,
            actual,
            expected - actual
        );
    }

    public RaceDemoResultDto runSafeDemo(int threads, int incrementsPerThread) {
        long expected = (long) threads * incrementsPerThread;
        long actual = runSafeCounter(threads, incrementsPerThread);
        return new RaceDemoResultDto(
            "safe",
            threads,
            incrementsPerThread,
            expected,
            actual,
            expected - actual
        );
    }

    private long runUnsafeCounter(int threads, int incrementsPerThread) {
        Counter counter = new Counter(false);
        runConcurrentIncrement(threads, incrementsPerThread, counter::increment);
        return counter.get();
    }

    private long runSafeCounter(int threads, int incrementsPerThread) {
        Counter counter = new Counter(true);
        runConcurrentIncrement(threads, incrementsPerThread, counter::increment);
        return counter.get();
    }

    private void runConcurrentIncrement(int threads, int incrementsPerThread, Runnable operation) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        List<Exception> failures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < incrementsPerThread; j++) {
                        operation.run();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    synchronized (failures) {
                        failures.add(ex);
                    }
                } catch (Exception ex) {
                    synchronized (failures) {
                        failures.add(ex);
                    }
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        try {
            boolean completed = doneLatch.await(20, TimeUnit.SECONDS);
            if (!completed) {
                throw new IllegalStateException("Race demo timed out");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Race demo interrupted", ex);
        } finally {
            executor.shutdown();
        }

        if (!failures.isEmpty()) {
            throw new IllegalStateException("Race demo execution failed", failures.get(0));
        }
    }

    private static final class Counter {
        private long value;
        private final boolean threadSafe;

        Counter(boolean threadSafe) {
            this.threadSafe = threadSafe;
        }

        void increment() {
            if (threadSafe) {
                synchronized (this) {
                    value++;
                }
            } else {
                value++; // небезопасно
            }
        }

        long get() {
            if (threadSafe) {
                synchronized (this) {
                    return value;
                }
            }
            return value;
        }
    }
}
