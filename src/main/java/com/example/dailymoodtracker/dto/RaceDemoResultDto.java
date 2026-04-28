package com.example.dailymoodtracker.dto;

public record RaceDemoResultDto(
    String mode,
    int threads,
    int incrementsPerThread,
    long expected,
    long actual,
    long lostUpdates
) { }
