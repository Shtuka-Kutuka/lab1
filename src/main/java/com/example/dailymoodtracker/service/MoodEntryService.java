package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.repository.MoodEntryRepository;
import com.example.dailymoodtracker.repository.MoodTypeRepository;
import com.example.dailymoodtracker.repository.UserRepository;
import com.example.dailymoodtracker.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MoodEntryService {

    private final MoodEntryRepository repository;
    private final UserRepository userRepo;
    private final GoalRepository goalRepo;
    private final MoodTypeRepository moodTypeRepo;

    public MoodEntryService(
        MoodEntryRepository repository,
        UserRepository userRepo,
        GoalRepository goalRepo,
        MoodTypeRepository moodTypeRepo) {
        this.repository = repository;
        this.userRepo = userRepo;
        this.goalRepo = goalRepo;
        this.moodTypeRepo = moodTypeRepo;
    }

    public List<MoodEntry> getByDate(LocalDate date) {
        return repository.findByEntryDate(date, Pageable.unpaged()).getContent();
    }

    public Optional<MoodEntry> getById(Long id) {
        return repository.findById(id);
    }

    public MoodEntry save(MoodEntry entry) {
        if (entry.getId() != null && repository.existsById(entry.getId())) {
            throw new IllegalArgumentException("Mood with id " + entry.getId() + " already exists");
        }

        if (entry.getMoodType() != null) {
            String name = entry.getMoodType().getName();
            MoodType mt = moodTypeRepo.findByName(name)
                .orElseGet(() -> moodTypeRepo.save(entry.getMoodType()));
            entry.setMoodType(mt);
        }

        return repository.save(entry);
    }

    @Transactional
    public MoodEntry update(Long id, MoodEntryDto dto) {
        MoodEntry entry = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mood not found: " + id));

        entry.setEntryDate(dto.date());

        if (dto.mood() != null && !dto.mood().isEmpty()) {
            MoodType mt = moodTypeRepo.findByName(dto.mood())
                .orElseGet(() -> moodTypeRepo.save(new MoodType(dto.mood(), null, null)));
            entry.setMoodType(mt);
        }

        return repository.save(entry);
    }

    public void delete(Long id) {
        MoodEntry entry = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mood not found: " + id));
        repository.delete(entry);
    }
}