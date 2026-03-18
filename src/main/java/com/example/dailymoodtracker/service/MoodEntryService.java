package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.model.User;
import com.example.dailymoodtracker.repository.MoodEntryRepository;
import com.example.dailymoodtracker.repository.MoodTypeRepository;
import com.example.dailymoodtracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MoodEntryService {

    private final MoodEntryRepository repository;
    private final MoodTypeRepository moodTypeRepo;

    private final UserRepository userRepository;

    public MoodEntryService(
        MoodEntryRepository repository,
        MoodTypeRepository moodTypeRepo,
        UserRepository userRepository) {

        this.repository = repository;
        this.moodTypeRepo = moodTypeRepo;
        this.userRepository = userRepository;
    }

    public List<MoodEntry> getByDate(LocalDate date) {
        return repository.findByEntryDate(date, Pageable.unpaged()).getContent();
    }

    public Optional<MoodEntry> getById(Long id) {
        return repository.findById(id);
    }

    public MoodEntry save(MoodEntry entry, String moodName) {

        if (entry.getEntryDate() == null) {
            throw new DataConflictException("Entry date cannot be null");
        }

        if (entry.getUser() == null || entry.getUser().getId() == null) {
            throw new DataConflictException("User is required");
        }

        Long userId = entry.getUser().getId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        entry.setUser(user);

        if (moodName != null && !moodName.isEmpty()) {
            MoodType mt = moodTypeRepo.findByName(moodName)
                .orElseGet(() -> moodTypeRepo.save(new MoodType(moodName, null, null)));

            entry.setMoodType(mt);
        }

        return repository.save(entry);
    }

    @Transactional
    public MoodEntry update(Long id, MoodEntryDto dto) {

        MoodEntry entry = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mood not found: " + id));

        if (dto.date() == null) {
            throw new DataConflictException("Date cannot be null");
        }

        entry.setEntryDate(dto.date());

        if (dto.userId() != null) {
            User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.userId()));

            entry.setUser(user);
        }

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