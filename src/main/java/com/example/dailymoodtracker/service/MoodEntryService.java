package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.repository.MoodEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoodEntryService {

    private final MoodEntryRepository repository;
    private final List<MoodEntry> fakeData = new ArrayList<>(List.of(
        // Steve — 2026-03-01
        create(1L, "happy",     LocalDate.of(2026, 3, 1)),
        create(2L, "tired",     LocalDate.of(2026, 3, 1)),
        create(3L, "focused",   LocalDate.of(2026, 3, 1)),

        create(4L, "motivated", LocalDate.of(2026, 3, 2)),
        create(5L, "excited",   LocalDate.of(2026, 3, 2)),
        create(6L, "stressed",  LocalDate.of(2026, 3, 3)),

        create(7L, "calm",      LocalDate.of(2026, 3, 3)),
        create(8L, "sleepy",    LocalDate.of(2026, 3, 4)),
        create(9L, "productive",LocalDate.of(2026, 3, 4)),
        create(10L,"chill",     LocalDate.of(2026, 3, 4)),
        create(11L,"anxious",   LocalDate.of(2026, 3, 4))
    ));

    public MoodEntryService(MoodEntryRepository repository) {
        this.repository = repository;
    }

    private static MoodEntry create(Long id, String mood, LocalDate date) {
        MoodEntry entry = new MoodEntry(mood, date);
        try {
            var idField = MoodEntry.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entry, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set id in test data", e);
        }
        return entry;
    }

    public List<MoodEntry> getByDate(LocalDate date) {
        if (date == null) {
            return List.of();
        }

        return fakeData.stream()
            .filter(e -> date.equals(e.getDate()))
            .collect(Collectors.toList());  // или .toList() если Java ≥ 16
    }

    public MoodEntry getById(Long id) {
        if (id == null) {
            return null;
        }
        return fakeData.stream()
            .filter(e -> id.equals(e.getId()))
            .findFirst()
            .orElse(null);
    }
}