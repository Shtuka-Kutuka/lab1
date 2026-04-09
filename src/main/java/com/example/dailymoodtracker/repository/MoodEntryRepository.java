package com.example.dailymoodtracker.repository;

import com.example.dailymoodtracker.model.MoodEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "moodType", "tags"})
    java.util.List<MoodEntry> findAll();

    @EntityGraph(attributePaths = {"moodType", "tags"})
    java.util.List<MoodEntry> findByUserId(Long userId);

    // ✅ JPQL (по вложенной сущности)
    @EntityGraph(attributePaths = {"user", "moodType", "tags"})
    @Query(
        value = """
        SELECT m FROM MoodEntry m
        JOIN m.moodType mt
        WHERE m.user.id = :userId AND mt.name = :moodName
        """,
        countQuery = """
        SELECT COUNT(m) FROM MoodEntry m
        JOIN m.moodType mt
        WHERE m.user.id = :userId AND mt.name = :moodName
        """
    )
    Page<MoodEntry> findComplex(Long userId, String moodName, Pageable pageable);

    // ✅ Native query
    @Query(
        value = """
            SELECT * FROM mood_entry m
            JOIN mood_type mt ON m.mood_type_id = mt.id
            WHERE m.user_id = :userId AND mt.name = :moodName
            """,
        countQuery = """
            SELECT COUNT(*) FROM mood_entry m
            JOIN mood_type mt ON m.mood_type_id = mt.id
            WHERE m.user_id = :userId AND mt.name = :moodName
            """,
        nativeQuery = true
    )
    Page<MoodEntry> findComplexNative(Long userId, String moodName, Pageable pageable);
}