package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.cache.MoodEntryQueryKey;
import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.MoodEntry;
import com.example.dailymoodtracker.model.MoodType;
import com.example.dailymoodtracker.model.Tag;
import com.example.dailymoodtracker.model.User;
import com.example.dailymoodtracker.repository.MoodEntryRepository;
import com.example.dailymoodtracker.repository.MoodTypeRepository;
import com.example.dailymoodtracker.repository.TagRepository;
import com.example.dailymoodtracker.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MoodEntryService {

    private final MoodEntryRepository repository;
    private final MoodTypeRepository moodTypeRepo;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    private final Map<MoodEntryQueryKey, Page<MoodEntry>> cache = new ConcurrentHashMap<>();

    public MoodEntryService(
        MoodEntryRepository repository,
        MoodTypeRepository moodTypeRepo,
        UserRepository userRepository,
        TagRepository tagRepository) {

        this.repository = repository;
        this.moodTypeRepo = moodTypeRepo;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    public List<MoodEntry> findAll() {
        return repository.findAll();
    }

    public List<MoodEntry> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    // ✅ JPQL + cache
    public Page<MoodEntry> findComplex(Long userId, String moodName, Pageable pageable) {

        MoodEntryQueryKey key = new MoodEntryQueryKey(
            userId, moodName, pageable.getPageNumber(), pageable.getPageSize()
        );

        return cache.computeIfAbsent(key, k -> {
            System.out.println("DB JPQL");
            return repository.findComplex(userId, moodName, pageable);
        });
    }

    // ✅ Native + cache
    public Page<MoodEntry> findComplexNative(Long userId, String moodName, Pageable pageable) {

        MoodEntryQueryKey key = new MoodEntryQueryKey(
            userId, moodName, pageable.getPageNumber(), pageable.getPageSize()
        );

        return cache.computeIfAbsent(key, k -> {
            System.out.println("DB NATIVE");
            return repository.findComplexNative(userId, moodName, pageable);
        });
    }

    public MoodEntry save(MoodEntry entry, MoodEntryDto dto) {

        if (entry.getEntryDate() == null) {
            throw new DataConflictException("Entry date cannot be null");
        }

        Long userId = entry.getUser().getId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        entry.setUser(user);

        if (dto.mood() != null) {
            MoodType mt = moodTypeRepo.findByName(dto.mood())
                .orElseGet(() -> moodTypeRepo.save(new MoodType(dto.mood(), null, null)));
            entry.setMoodType(mt);
        }

        if (dto.tagIds() != null) {
            Set<Tag> tags = dto.tagIds().stream()
                .map(id -> tagRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id)))
                .collect(Collectors.toSet());

            entry.setTags(tags);
        }

        cache.clear(); // ✅ инвалидация
        return repository.save(entry);
    }

    @Transactional
    public MoodEntry update(Long id, MoodEntryDto dto) {

        MoodEntry entry = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mood not found: " + id));

        entry.setEntryDate(dto.date());

        cache.clear(); // ✅ инвалидация
        return repository.save(entry);
    }

    public void delete(Long id) {

        MoodEntry entry = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mood not found: " + id));

        repository.delete(entry);
        cache.clear(); // ✅ инвалидация
    }
}