package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.cache.MoodEntryQueryKey;
import com.example.dailymoodtracker.dto.MoodEntryDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.*;
import com.example.dailymoodtracker.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MoodEntryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoodEntryService.class);

    private final MoodEntryRepository repository;
    private final MoodTypeRepository moodTypeRepo;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    private final Map<MoodEntryQueryKey, Page<MoodEntry>> cache = new ConcurrentHashMap<>();

    public MoodEntryService(
        MoodEntryRepository repository,
        MoodTypeRepository moodTypeRepo,
        UserRepository userRepository,
        TagRepository tagRepository
    ) {
        this.repository = repository;
        this.moodTypeRepo = moodTypeRepo;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    // ===================== BULK WITHOUT TRANSACTION =====================

    public List<MoodEntry> saveAll(List<MoodEntryDto> dtos) {

        if (dtos == null || dtos.isEmpty()) {
            throw new DataConflictException("Mood entries list cannot be empty");
        }

        return dtos.stream()
            .map(this::buildEntityFromDto)
            .map(repository::save) // ❗ сохраняется по одному
            .peek(e -> LOGGER.debug("Saved entry id={}", e.getId()))
            .toList();
    }

    // ===================== BULK WITH TRANSACTION =====================

    @Transactional
    public List<MoodEntry> saveAllTransactional(List<MoodEntryDto> dtos) {

        if (dtos == null || dtos.isEmpty()) {
            throw new DataConflictException("Mood entries list cannot be empty");
        }

        List<MoodEntry> result = dtos.stream()
            .map(this::buildEntityFromDto)
            .map(repository::save)
            .toList();

        cache.clear();
        LOGGER.debug("Cache cleared after BULK TX SAVE");

        return result;
    }

    // ===================== HELPER =====================

    private MoodEntry buildEntityFromDto(MoodEntryDto dto) {

        // 💣 Искусственная ошибка для демонстрации транзакций
        if ("ERROR".equalsIgnoreCase(dto.mood())) {
            throw new DataConflictException("Artificial failure triggered");
        }

        MoodEntry entry = new MoodEntry();
        entry.setEntryDate(dto.date());

        User user = userRepository.findById(dto.userId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.userId()));

        entry.setUser(user);

        Optional.ofNullable(dto.mood())
            .ifPresent(mood -> {
                MoodType mt = moodTypeRepo.findByName(mood)
                    .orElseGet(() -> moodTypeRepo.save(new MoodType(mood, null, null)));
                entry.setMoodType(mt);
            });

        Set<Tag> tags = Optional.ofNullable(dto.tagIds())
            .orElse(Collections.emptyList())
            .stream()
            .map(id -> tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id)))
            .collect(Collectors.toSet());

        entry.setTags(tags);

        return entry;
    }

    // ===================== SINGLE SAVE =====================

    public MoodEntry save(MoodEntry entry, MoodEntryDto dto) {

        if (entry.getEntryDate() == null) {
            throw new DataConflictException("Entry date cannot be null");
        }

        Long userId = Optional.ofNullable(entry.getUser())
            .map(User::getId)
            .orElseThrow(() -> new DataConflictException("UserId cannot be null"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        entry.setUser(user);

        Optional.ofNullable(dto.mood())
            .ifPresent(mood -> {
                MoodType mt = moodTypeRepo.findByName(mood)
                    .orElseGet(() -> moodTypeRepo.save(new MoodType(mood, null, null)));
                entry.setMoodType(mt);
            });

        Set<Tag> tags = Optional.ofNullable(dto.tagIds())
            .orElse(Collections.emptyList())
            .stream()
            .map(id -> tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id)))
            .collect(Collectors.toSet());

        entry.setTags(tags);

        cache.clear();

        return repository.save(entry);
    }

    // ===================== READ =====================

    public List<MoodEntry> findAll() {
        return repository.findAll();
    }

    public List<MoodEntry> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public Page<MoodEntry> findComplex(Long userId, String moodName, Pageable pageable) {

        MoodEntryQueryKey key = new MoodEntryQueryKey(
            userId, moodName, pageable.getPageNumber(), pageable.getPageSize()
        );

        return Optional.ofNullable(cache.get(key))
            .orElseGet(() -> {
                Page<MoodEntry> page = repository.findComplex(userId, moodName, pageable);
                cache.put(key, page);
                return page;
            });
    }

    public Page<MoodEntry> findComplexNative(Long userId, String moodName, Pageable pageable) {

        MoodEntryQueryKey key = new MoodEntryQueryKey(
            userId, moodName, pageable.getPageNumber(), pageable.getPageSize()
        );

        return cache.computeIfAbsent(key, k -> {

            Page<MoodEntry> page = repository.findComplexNative(userId, moodName, pageable);
            List<MoodEntry> entries = page.getContent();

            if (entries.isEmpty()) {
                return page;
            }

            Map<Long, User> users = userRepository.findAllById(
                entries.stream().map(MoodEntry::getUserId).distinct().toList()
            ).stream().collect(Collectors.toMap(User::getId, u -> u));

            Map<Long, MoodType> moods = moodTypeRepo.findAllById(
                entries.stream().map(MoodEntry::getMoodTypeId).distinct().toList()
            ).stream().collect(Collectors.toMap(MoodType::getId, m -> m));

            Map<Long, Set<Tag>> tagsMap = loadTags(entries);

            entries.forEach(e -> {
                e.setUser(users.get(e.getUserId()));
                e.setMoodType(moods.get(e.getMoodTypeId()));
                e.setTags(tagsMap.getOrDefault(e.getId(), Collections.emptySet()));
            });

            return page;
        });
    }

    private Map<Long, Set<Tag>> loadTags(List<MoodEntry> entries) {

        List<Long> ids = entries.stream()
            .map(MoodEntry::getId)
            .distinct()
            .toList();

        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Object[]> rows = tagRepository.findTagsByMoodEntryIds(ids);

        return rows.stream().collect(Collectors.groupingBy(
            row -> (Long) row[0],
            Collectors.mapping(row -> (Tag) row[1], Collectors.toSet())
        ));
    }

    public void invalidateCache() {
        cache.clear();
    }

    @Transactional
    public MoodEntry update(Long id, MoodEntryDto dto) {

        MoodEntry entry = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mood not found: " + id));

        entry.setEntryDate(dto.date());

        cache.clear();
        return repository.save(entry);
    }

    public void delete(Long id) {

        MoodEntry entry = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mood not found: " + id));

        repository.delete(entry);

        cache.clear();
    }
}