package com.example.dailymoodtracker.repository;

import com.example.dailymoodtracker.model.MoodEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {

    @Query("SELECT m FROM MoodEntry m WHERE m.moodType.name = :typeName")
    Page<MoodEntry> findByMoodTypeName(@Param("typeName") String typeName, Pageable pageable);

    @Query(value = "SELECT me.* FROM mood_entry me " +
        "JOIN mood_entry_tag met ON me.id = met.mood_entry_id " +
        "JOIN tag t ON met.tag_id = t.id WHERE t.name = :tagName",
        nativeQuery = true)
    Page<MoodEntry> findByTagNameNative(@Param("tagName") String tagName, Pageable pageable);

    Page<MoodEntry> findByEntryDate(LocalDate entryDate, Pageable pageable);

    @Query("SELECT m FROM MoodEntry m " +
        "LEFT JOIN FETCH m.moodType " +
        "LEFT JOIN FETCH m.user " +
        "LEFT JOIN FETCH m.tags")
    List<MoodEntry> findAllWithRelations();
}